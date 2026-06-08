package com.furniturestore.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "deliveries", schema = "logistics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_number", nullable = false, unique = true, length = 30)
    private String deliveryNumber;

    // LAZY fetches — DTO projection prevents recursive JSON serialization
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private DeliveryStatus status;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "delivered_date")
    private LocalDate deliveredDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryCost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DeliveryItem> items;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now();
        if (this.deliveryCost == null) this.deliveryCost = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
