package com.furniturestore.employee.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private Long positionId;
    private String positionTitle;
    private Long departmentId;
    private String departmentName;
    private Long userId;
    private LocalDate hireDate;
    private BigDecimal salary;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
