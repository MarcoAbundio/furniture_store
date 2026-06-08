package com.furniturestore.employee.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PositionRequest {
    @NotBlank @Size(max = 100)
    private String title;
    @NotNull
    private Long departmentId;
    @DecimalMin("0.0")
    private BigDecimal baseSalary;
}
