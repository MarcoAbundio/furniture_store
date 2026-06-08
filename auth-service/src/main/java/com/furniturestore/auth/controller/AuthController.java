package com.furniturestore.auth.controller;

import com.furniturestore.auth.dto.request.*;
import com.furniturestore.auth.dto.response.*;
import com.furniturestore.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Register user", description = "Create a new system user (Admin only)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users", description = "Paginated list of active users (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        return ResponseEntity.ok(authService.getAllUsers(PageRequest.of(page, size, Sort.by(sort))));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Enable or disable user (Admin only)")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(authService.updateUserStatus(id, active));
    }

    @PatchMapping("/users/{id}/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
