package com.furniturestore.delivery.controller;

import com.furniturestore.delivery.dto.request.*;
import com.furniturestore.delivery.dto.response.*;
import com.furniturestore.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer and address management for home deliveries")
public class CustomerController {

    private final DeliveryService deliveryService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Register a new customer")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createCustomer(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all customers (paginated)")
    public ResponseEntity<Page<CustomerResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sort) {
        return ResponseEntity.ok(deliveryService.getAllCustomers(PageRequest.of(page, size, Sort.by(sort))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getCustomerById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Search customers by name, phone, or email")
    public ResponseEntity<Page<CustomerResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(deliveryService.searchCustomers(q, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Update customer data")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(deliveryService.updateCustomer(id, request));
    }

    @PostMapping("/addresses")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Add delivery address to a customer")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.addAddress(request));
    }

    @GetMapping("/{customerId}/addresses")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all addresses for a customer")
    public ResponseEntity<List<AddressResponse>> getAddresses(@PathVariable Long customerId) {
        return ResponseEntity.ok(deliveryService.getAddressesByCustomer(customerId));
    }

    @PatchMapping("/addresses/{addressId}/set-default")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Set an address as default for its customer")
    public ResponseEntity<AddressResponse> setDefault(@PathVariable Long addressId) {
        return ResponseEntity.ok(deliveryService.setDefaultAddress(addressId));
    }
}
