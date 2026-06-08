package com.furniturestore.auth.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private Long expiresIn;
}
