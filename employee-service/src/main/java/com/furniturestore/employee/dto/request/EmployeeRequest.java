package com.furniturestore.employee.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeRequest {
    @NotBlank @Size(max = 20)
    private String employeeCode;
    @NotBlank @Size(max = 100)
    private String firstName;
    @NotBlank @Size(max = 100)
    private String lastName;
    @NotBlank @Email
    private String email;
    @Size(max = 20)
    private String phone;
    @NotNull
    private Long positionId;
    private Long userId;
    @NotNull
    private LocalDate hireDate;
    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salary;
}
