package com.furniturestore.delivery.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String rfc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
