package com.furniturestore.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", schema = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String sku;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(name = "weight_kg", precision = 8, scale = 2)
    private BigDecimal weightKg;
    @Column(length = 100)
    private String dimensions;
    @Column(length = 100)
    private String material;
    @Column(length = 50)
    private String color;
    @Column(length = 100)
    private String brand;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }
    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
