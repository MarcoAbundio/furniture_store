package com.furniturestore.delivery.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddressResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String street;
    private String extNumber;
    private String intNumber;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String references;
    private Boolean isDefault;
    private String fullAddress;
    private LocalDateTime createdAt;
}
