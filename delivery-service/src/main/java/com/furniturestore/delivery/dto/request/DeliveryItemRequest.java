package com.furniturestore.delivery.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryItemRequest {
    @NotNull
    private Long productId;
    @NotNull @Min(1)
    private Integer quantity;
    @NotNull @DecimalMin("0.0")
    private BigDecimal unitPrice;
}
