package com.furniturestore.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock", schema = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "min_stock", nullable = false)
    private Integer minStock;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @PrePersist @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
