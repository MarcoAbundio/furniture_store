package com.furniturestore.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockAdjustmentRequest {
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank
    private String type; // INCREMENT or DECREMENT

    private String reason;
}
