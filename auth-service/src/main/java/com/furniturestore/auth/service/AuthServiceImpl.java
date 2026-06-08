package com.furniturestore.auth.service;

import com.furniturestore.auth.dto.request.*;
import com.furniturestore.auth.dto.response.*;
import com.furniturestore.auth.exception.*;
import com.furniturestore.auth.mapper.UserMapper;
import com.furniturestore.auth.model.User;
import com.furniturestore.auth.repository.*;
import com.furniturestore.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken(
                user.getUsername(), user.getRole().getName(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new BusinessException("Username already exists: " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BusinessException("Email already registered: " + request.getEmail());

        var role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllActive(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsActive(active);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new BusinessException("Current password is incorrect");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
