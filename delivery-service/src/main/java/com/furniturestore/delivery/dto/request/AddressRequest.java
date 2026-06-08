package com.furniturestore.delivery.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddressRequest {
    @NotNull
    private Long customerId;
    @NotBlank @Size(max = 255)
    private String street;
    @NotBlank @Size(max = 20)
    private String extNumber;
    @Size(max = 20)
    private String intNumber;
    @NotBlank @Size(max = 100)
    private String neighborhood;
    @NotBlank @Size(max = 100)
    private String city;
    @NotBlank @Size(max = 100)
    private String state;
    @NotBlank @Size(max = 10)
    private String zipCode;
    @Size(max = 50)
    private String country;
    private String references;
    private Boolean isDefault;
}
