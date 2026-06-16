package com.furniturestore.delivery.service;

import com.furniturestore.delivery.dto.request.*;
import com.furniturestore.delivery.dto.response.*;
import com.furniturestore.delivery.exception.*;
import com.furniturestore.delivery.mapper.DeliveryMapper;
import com.furniturestore.delivery.model.*;
import com.furniturestore.delivery.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock CustomerRepository customerRepository;
    @Mock AddressRepository addressRepository;
    @Mock DeliveryRepository deliveryRepository;
    @Mock DeliveryStatusRepository statusRepository;
    @Mock DeliveryMapper deliveryMapper;
    @InjectMocks DeliveryServiceImpl deliveryService;

    private Customer customer;
    private Address address;
    private DeliveryStatus cancelledStatus;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().id(1L).firstName("María").lastName("López")
                .phone("5512345678").email("maria@test.com").build();
        address = Address.builder().id(1L).customer(customer).street("Insurgentes")
                .extNumber("123").neighborhood("Roma").city("CDMX").state("CDMX")
                .zipCode("06700").country("México").isDefault(true).build();
        cancelledStatus = DeliveryStatus.builder().id(5L).code("CANCELLED").description("Cancelado").build();
    }

    @Test
    @DisplayName("createCustomer - should throw when phone already registered")
    void createCustomer_phoneExists_throws() {
        var req = CustomerRequest.builder().firstName("Ana").lastName("García")
                .phone("5512345678").build();
        when(customerRepository.existsByPhone("5512345678")).thenReturn(true);

        assertThatThrownBy(() -> deliveryService.createCustomer(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Phone already registered");
    }

    @Test
    @DisplayName("createCustomer - should create customer successfully")
    void createCustomer_success() {
        var req = CustomerRequest.builder().firstName("Luis").lastName("Pérez")
                .phone("5598765432").email("luis@test.com").build();
        var expected = CustomerResponse.builder().id(2L).firstName("Luis").build();

        when(customerRepository.existsByPhone("5598765432")).thenReturn(false);
        when(customerRepository.existsByEmail("luis@test.com")).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(customer);
        when(deliveryMapper.toCustomerResponse(any())).thenReturn(expected);

        CustomerResponse result = deliveryService.createCustomer(req);
        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("createDelivery - should throw when scheduled date is in the past")
    void createDelivery_pastDate_throws() {
        var item = DeliveryItemRequest.builder().productId(1L).quantity(1)
                .unitPrice(new BigDecimal("1000")).build();
        var req = DeliveryRequest.builder().customerId(1L).addressId(1L).employeeId(1L)
                .scheduledDate(LocalDate.now().minusDays(1)).items(List.of(item)).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        assertThatThrownBy(() -> deliveryService.createDelivery(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Scheduled date cannot be in the past");
    }

    @Test
    @DisplayName("createDelivery - should throw when address doesn't belong to customer")
    void createDelivery_addressMismatch_throws() {
        var otherCustomer = Customer.builder().id(99L).build();
        var wrongAddress = Address.builder().id(1L).customer(otherCustomer).build();

        var item = DeliveryItemRequest.builder().productId(1L).quantity(1)
                .unitPrice(new BigDecimal("1000")).build();
        var req = DeliveryRequest.builder().customerId(1L).addressId(1L).employeeId(1L)
                .scheduledDate(LocalDate.now().plusDays(1)).items(List.of(item)).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(wrongAddress));

        assertThatThrownBy(() -> deliveryService.createDelivery(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Address does not belong");
    }

    @Test
    @DisplayName("updateDeliveryStatus - should throw when delivery is already cancelled")
    void updateStatus_alreadyCancelled_throws() {
        var delivery = Delivery.builder().id(1L).status(cancelledStatus).build();
        var req = new DeliveryStatusUpdateRequest("IN_TRANSIT", null);

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.updateDeliveryStatus(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot update status of a cancelled delivery");
    }

    @Test
    @DisplayName("cancelDelivery - should throw when delivery is already delivered")
    void cancelDelivery_alreadyDelivered_throws() {
        var deliveredStatus = DeliveryStatus.builder().code("DELIVERED").build();
        var delivery = Delivery.builder().id(1L).status(deliveredStatus).build();

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.cancelDelivery(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot cancel a delivery that has already been delivered");
    }
}
