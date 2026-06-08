package com.furniturestore.employee.controller;

import com.furniturestore.employee.dto.request.PositionRequest;
import com.furniturestore.employee.dto.response.PositionResponse;
import com.furniturestore.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Tag(name = "Positions", description = "Job position management endpoints")
public class PositionController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create position (Admin only)")
    public ResponseEntity<PositionResponse> create(@Valid @RequestBody PositionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createPosition(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all positions (paginated)")
    public ResponseEntity<Page<PositionResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(employeeService.getAllPositions(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get position by ID")
    public ResponseEntity<PositionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getPositionById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update position (Admin only)")
    public ResponseEntity<PositionResponse> update(
            @PathVariable Long id, @Valid @RequestBody PositionRequest request) {
        return ResponseEntity.ok(employeeService.updatePosition(id, request));
    }
}
