package com.furniturestore.employee.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", schema = "hr")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(length = 20)
    private String phone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }
    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
