package com.furniturestore.product.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {
    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private Integer quantity;
    private Integer minStock;
    private Boolean isLowStock;
    private LocalDateTime updatedAt;
}
