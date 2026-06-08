package com.furniturestore.employee.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "departments", schema = "hr")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); if (this.isActive == null) this.isActive = true; }
}
