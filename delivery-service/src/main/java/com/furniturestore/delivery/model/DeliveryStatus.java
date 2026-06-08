package com.furniturestore.delivery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_status_catalog", schema = "logistics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @Column(nullable = false, length = 100)
    private String description;
}
