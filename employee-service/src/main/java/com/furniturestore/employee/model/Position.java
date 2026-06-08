package com.furniturestore.employee.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions", schema = "hr")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Position {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    @Column(name = "base_salary", precision = 10, scale = 2)
    private BigDecimal baseSalary;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); if (this.isActive == null) this.isActive = true; }
}
