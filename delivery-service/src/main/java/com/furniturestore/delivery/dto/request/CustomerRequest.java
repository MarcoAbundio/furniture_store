package com.furniturestore.delivery.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerRequest {
    @NotBlank @Size(max = 100)
    private String firstName;
    @NotBlank @Size(max = 100)
    private String lastName;
    @Email @Size(max = 255)
    private String email;
    @NotBlank @Size(max = 20)
    private String phone;
    @Size(max = 13)
    private String rfc;
}
