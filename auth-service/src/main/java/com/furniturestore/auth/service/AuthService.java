package com.furniturestore.auth.service;

import com.furniturestore.auth.dto.request.*;
import com.furniturestore.auth.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUserStatus(Long id, boolean active);
    void changePassword(Long id, ChangePasswordRequest request);
}
