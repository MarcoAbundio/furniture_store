package com.furniturestore.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    private String description;
    private Long parentId;
}
