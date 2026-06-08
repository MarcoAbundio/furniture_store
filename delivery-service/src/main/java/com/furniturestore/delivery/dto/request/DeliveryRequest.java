package com.furniturestore.delivery.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryRequest {
    @NotNull
    private Long customerId;
    @NotNull
    private Long addressId;
    @NotNull
    private Long employeeId;
    @NotNull
    private LocalDate scheduledDate;
    @DecimalMin("0.0")
    private BigDecimal deliveryCost;
    private String notes;
    @NotEmpty(message = "At least one item is required")
    private List<DeliveryItemRequest> items;
}
