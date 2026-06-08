package com.furniturestore.product.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
