package com.furniturestore.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "SKU is required")
    @Size(max = 50)
    private String sku;

    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @DecimalMin(value = "0.0")
    private BigDecimal weightKg;

    @Size(max = 100)
    private String dimensions;

    @Size(max = 100)
    private String material;

    @Size(max = 50)
    private String color;

    @Size(max = 100)
    private String brand;

    @NotNull
    @Min(0)
    private Integer initialStock;

    @Min(0)
    private Integer minStock;
}
