package com.furniturestore.delivery.service;

import com.furniturestore.delivery.dto.request.*;
import com.furniturestore.delivery.dto.response.*;
import com.furniturestore.delivery.exception.*;
import com.furniturestore.delivery.mapper.DeliveryMapper;
import com.furniturestore.delivery.model.*;
import com.furniturestore.delivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryStatusRepository statusRepository;
    private final DeliveryMapper deliveryMapper;

    // Thread-safe counter for delivery number generation
    private final AtomicLong sequence = new AtomicLong(1);

    // ===== CUSTOMER =====
    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest req) {
        if (req.getEmail() != null && customerRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email already registered: " + req.getEmail());
        if (customerRepository.existsByPhone(req.getPhone()))
            throw new BusinessException("Phone already registered: " + req.getPhone());

        var customer = Customer.builder()
                .firstName(req.getFirstName()).lastName(req.getLastName())
                .email(req.getEmail()).phone(req.getPhone()).rfc(req.getRfc()).build();

        return deliveryMapper.toCustomerResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        return deliveryMapper.toCustomerResponse(customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(deliveryMapper::toCustomerResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String query, Pageable pageable) {
        return customerRepository.search(query, pageable).map(deliveryMapper::toCustomerResponse);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest req) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        if (req.getEmail() != null && !req.getEmail().equals(customer.getEmail())
                && customerRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email already in use: " + req.getEmail());

        customer.setFirstName(req.getFirstName()); customer.setLastName(req.getLastName());
        customer.setEmail(req.getEmail()); customer.setPhone(req.getPhone()); customer.setRfc(req.getRfc());
        return deliveryMapper.toCustomerResponse(customerRepository.save(customer));
    }

    // ===== ADDRESS =====
    @Override
    @Transactional
    public AddressResponse addAddress(AddressRequest req) {
        var customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + req.getCustomerId()));

        // If marked default, clear previous default
        if (Boolean.TRUE.equals(req.getIsDefault())) {
            addressRepository.clearDefaultForCustomer(req.getCustomerId());
        }

        var address = Address.builder()
                .customer(customer).street(req.getStreet()).extNumber(req.getExtNumber())
                .intNumber(req.getIntNumber()).neighborhood(req.getNeighborhood())
                .city(req.getCity()).state(req.getState()).zipCode(req.getZipCode())
                .country(req.getCountry() != null ? req.getCountry() : "México")
                .references(req.getReferences())
                .isDefault(Boolean.TRUE.equals(req.getIsDefault())).build();

        return deliveryMapper.toAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId))
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        return addressRepository.findByCustomerId(customerId)
                .stream().map(deliveryMapper::toAddressResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId) {
        var address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        addressRepository.clearDefaultForCustomer(address.getCustomer().getId());
        address.setIsDefault(true);
        return deliveryMapper.toAddressResponse(addressRepository.save(address));
    }

    // ===== DELIVERY =====
    @Override
    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest req) {
        var customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + req.getCustomerId()));
        var address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + req.getAddressId()));

        if (!address.getCustomer().getId().equals(req.getCustomerId()))
            throw new BusinessException("Address does not belong to the specified customer");

        if (req.getScheduledDate().isBefore(LocalDate.now()))
            throw new BusinessException("Scheduled date cannot be in the past");

        var pendingStatus = statusRepository.findByCode("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Status PENDING not found in catalog"));

        // Calculate total from items
        BigDecimal total = req.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var delivery = Delivery.builder()
                .deliveryNumber(generateDeliveryNumber())
                .customer(customer).address(address)
                .employeeId(req.getEmployeeId())
                .status(pendingStatus)
                .scheduledDate(req.getScheduledDate())
                .totalAmount(total)
                .deliveryCost(req.getDeliveryCost() != null ? req.getDeliveryCost() : BigDecimal.ZERO)
                .notes(req.getNotes()).build();

        var savedDelivery = deliveryRepository.save(delivery);

        // Build items
        List<DeliveryItem> items = req.getItems().stream().map(itemReq ->
                DeliveryItem.builder()
                        .delivery(savedDelivery)
                        .productId(itemReq.getProductId())
                        .quantity(itemReq.getQuantity())
                        .unitPrice(itemReq.getUnitPrice())
                        .build()
        ).toList();
        savedDelivery.setItems(items);

        return deliveryMapper.toDeliveryResponse(deliveryRepository.save(savedDelivery));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {
        return deliveryMapper.toDeliveryResponse(deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByNumber(String deliveryNumber) {
        return deliveryMapper.toDeliveryResponse(deliveryRepository.findByDeliveryNumber(deliveryNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + deliveryNumber)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAllWithDetails(pageable).map(deliveryMapper::toDeliveryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliveriesByCustomer(Long customerId, Pageable pageable) {
        return deliveryRepository.findByCustomerId(customerId, pageable).map(deliveryMapper::toDeliveryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliveriesByEmployee(Long employeeId, Pageable pageable) {
        return deliveryRepository.findByEmployeeId(employeeId, pageable).map(deliveryMapper::toDeliveryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliveriesByStatus(String statusCode, Pageable pageable) {
        return deliveryRepository.findByStatusCode(statusCode.toUpperCase(), pageable)
                .map(deliveryMapper::toDeliveryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliveriesByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        if (from.isAfter(to)) throw new BusinessException("'from' date must be before 'to' date");
        return deliveryRepository.findByScheduledDateBetween(from, to, pageable)
                .map(deliveryMapper::toDeliveryResponse);
    }

    @Override
    @Transactional
    public DeliveryResponse updateDeliveryStatus(Long id, DeliveryStatusUpdateRequest req) {
        var delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + id));

        // Business rule: can't change status of cancelled delivery
        if ("CANCELLED".equals(delivery.getStatus().getCode()))
            throw new BusinessException("Cannot update status of a cancelled delivery");

        var newStatus = statusRepository.findByCode(req.getStatusCode().toUpperCase())
                .orElseThrow(() -> new BusinessException("Invalid status code: " + req.getStatusCode()));

        delivery.setStatus(newStatus);
        if (req.getNotes() != null) delivery.setNotes(req.getNotes());
        if ("DELIVERED".equals(newStatus.getCode())) delivery.setDeliveredDate(LocalDate.now());

        return deliveryMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public void cancelDelivery(Long id) {
        var delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + id));
        if ("DELIVERED".equals(delivery.getStatus().getCode()))
            throw new BusinessException("Cannot cancel a delivery that has already been delivered");

        var cancelledStatus = statusRepository.findByCode("CANCELLED")
                .orElseThrow(() -> new ResourceNotFoundException("Status CANCELLED not found in catalog"));
        delivery.setStatus(cancelledStatus);
        deliveryRepository.save(delivery);
    }

    private String generateDeliveryNumber() {
        String prefix = "DEL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String number;
        do {
            number = prefix + String.format("%04d", sequence.getAndIncrement());
        } while (deliveryRepository.existsByDeliveryNumber(number));
        return number;
    }
}
