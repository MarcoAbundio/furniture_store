package com.furniturestore.auth.service;

import com.furniturestore.auth.dto.request.*;
import com.furniturestore.auth.dto.response.*;
import com.furniturestore.auth.exception.*;
import com.furniturestore.auth.mapper.UserMapper;
import com.furniturestore.auth.model.*;
import com.furniturestore.auth.repository.*;
import com.furniturestore.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserMapper userMapper;

    @InjectMocks AuthServiceImpl authService;

    private Role adminRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder().id(1L).name("ROLE_ADMIN").build();
        testUser = User.builder().id(1L).username("admin").email("admin@store.com")
                .password("encoded_pass").role(adminRole).isActive(true).build();
    }

    @Test
    @DisplayName("Login - should return AuthResponse with token on success")
    void login_success() {
        var request = new LoginRequest("admin", "password123");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt_token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        AuthResponse result = authService.login(request);

        assertThat(result.getToken()).isEqualTo("jwt_token");
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getRole()).isEqualTo("ROLE_ADMIN");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Register - should throw BusinessException if username already exists")
    void register_usernameExists_throwsException() {
        var request = new RegisterRequest("admin", "admin@test.com", "pass1234", 1L);
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("Register - should create user successfully")
    void register_success() {
        var request = new RegisterRequest("cashier1", "cashier@store.com", "pass1234", 2L);
        var cashierRole = Role.builder().id(2L).name("ROLE_CASHIER").build();
        var savedUser = User.builder().id(2L).username("cashier1").email("cashier@store.com")
                .role(cashierRole).isActive(true).build();
        var expectedResponse = UserResponse.builder().id(2L).username("cashier1").build();

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(cashierRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = authService.register(request);

        assertThat(result.getUsername()).isEqualTo("cashier1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getUserById - should throw ResourceNotFoundException when user not found")
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("changePassword - should throw BusinessException for wrong current password")
    void changePassword_wrongCurrentPassword() {
        var request = new ChangePasswordRequest("wrongPass", "newPass123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encoded_pass")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Current password is incorrect");
    }
}
