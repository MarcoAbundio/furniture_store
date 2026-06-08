package com.furniturestore.delivery.controller;

import com.furniturestore.delivery.dto.request.*;
import com.furniturestore.delivery.dto.response.*;
import com.furniturestore.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Home delivery management endpoints")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Create a new home delivery order")
    public ResponseEntity<DeliveryResponse> create(@Valid @RequestBody DeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all deliveries (paginated)")
    public ResponseEntity<Page<DeliveryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledDate") String sort) {
        return ResponseEntity.ok(deliveryService.getAllDeliveries(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get delivery by ID")
    public ResponseEntity<DeliveryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @GetMapping("/number/{deliveryNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get delivery by delivery number (e.g. DEL-20240601-0001)")
    public ResponseEntity<DeliveryResponse> getByNumber(@PathVariable String deliveryNumber) {
        return ResponseEntity.ok(deliveryService.getDeliveryByNumber(deliveryNumber));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all deliveries for a specific customer")
    public ResponseEntity<Page<DeliveryResponse>> getByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByCustomer(customerId, PageRequest.of(page, size)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all deliveries assigned to an employee")
    public ResponseEntity<Page<DeliveryResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByEmployee(employeeId, PageRequest.of(page, size)));
    }

    @GetMapping("/status/{statusCode}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get deliveries by status (PENDING, SCHEDULED, IN_TRANSIT, DELIVERED, CANCELLED, FAILED)")
    public ResponseEntity<Page<DeliveryResponse>> getByStatus(
            @PathVariable String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(statusCode, PageRequest.of(page, size)));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get deliveries scheduled between two dates")
    public ResponseEntity<Page<DeliveryResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByDateRange(from, to, PageRequest.of(page, size)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Update delivery status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long id, @Valid @RequestBody DeliveryStatusUpdateRequest request) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Cancel a delivery (Admin only)")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        deliveryService.cancelDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
