package com.furniturestore.delivery.service;

import com.furniturestore.delivery.dto.request.*;
import com.furniturestore.delivery.dto.response.*;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.List;

public interface DeliveryService {
    // Customer
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomerById(Long id);
    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    Page<CustomerResponse> searchCustomers(String query, Pageable pageable);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    // Address
    AddressResponse addAddress(AddressRequest request);
    List<AddressResponse> getAddressesByCustomer(Long customerId);
    AddressResponse setDefaultAddress(Long addressId);

    // Delivery
    DeliveryResponse createDelivery(DeliveryRequest request);
    DeliveryResponse getDeliveryById(Long id);
    DeliveryResponse getDeliveryByNumber(String deliveryNumber);
    Page<DeliveryResponse> getAllDeliveries(Pageable pageable);
    Page<DeliveryResponse> getDeliveriesByCustomer(Long customerId, Pageable pageable);
    Page<DeliveryResponse> getDeliveriesByEmployee(Long employeeId, Pageable pageable);
    Page<DeliveryResponse> getDeliveriesByStatus(String statusCode, Pageable pageable);
    Page<DeliveryResponse> getDeliveriesByDateRange(LocalDate from, LocalDate to, Pageable pageable);
    DeliveryResponse updateDeliveryStatus(Long id, DeliveryStatusUpdateRequest request);
    void cancelDelivery(Long id);
}
