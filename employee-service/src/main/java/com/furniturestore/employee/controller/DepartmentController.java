package com.furniturestore.employee.controller;

import com.furniturestore.employee.dto.request.DepartmentRequest;
import com.furniturestore.employee.dto.response.DepartmentResponse;
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
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management endpoints")
public class DepartmentController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create department (Admin only)")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createDepartment(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get all departments (paginated)")
    public ResponseEntity<Page<DepartmentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(employeeService.getAllDepartments(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<DepartmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update department (Admin only)")
    public ResponseEntity<DepartmentResponse> update(
            @PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(employeeService.updateDepartment(id, request));
    }
}
