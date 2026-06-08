package com.furniturestore.employee.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartmentRequest {
    @NotBlank @Size(max = 100)
    private String name;
    private String description;
}
