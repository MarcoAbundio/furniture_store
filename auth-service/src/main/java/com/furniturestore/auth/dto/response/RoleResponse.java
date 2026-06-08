package com.furniturestore.auth.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
