package com.furniturestore.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryStatusUpdateRequest {
    @NotBlank(message = "Status code is required (PENDING, SCHEDULED, IN_TRANSIT, DELIVERED, CANCELLED, FAILED)")
    private String statusCode;
    private String notes;
}
