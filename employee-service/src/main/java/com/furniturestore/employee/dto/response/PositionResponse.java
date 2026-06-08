package com.furniturestore.employee.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PositionResponse {
    private Long id;
    private String title;
    private Long departmentId;
    private String departmentName;
    private BigDecimal baseSalary;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
