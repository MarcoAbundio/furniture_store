package com.furniturestore.delivery.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryResponse {
    private Long id;
    private String deliveryNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long addressId;
    private String deliveryAddress;
    private Long employeeId;
    private Long statusId;
    private String statusCode;
    private String statusDescription;
    private LocalDate scheduledDate;
    private LocalDate deliveredDate;
    private BigDecimal totalAmount;
    private BigDecimal deliveryCost;
    private String notes;
    private List<DeliveryItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
