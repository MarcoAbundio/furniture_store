package com.furniturestore.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses", schema = "logistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @Column(nullable = false, length = 255)
    private String street;
    @Column(name = "ext_number", nullable = false, length = 20)
    private String extNumber;
    @Column(name = "int_number", length = 20)
    private String intNumber;
    @Column(nullable = false, length = 100)
    private String neighborhood;
    @Column(nullable = false, length = 100)
    private String city;
    @Column(nullable = false, length = 100)
    private String state;
    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;
    @Column(nullable = false, length = 50)
    private String country;
    @Column(name = "address_reference", columnDefinition = "TEXT")
    private String references;
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.country == null)
            this.country = "México";
        if (this.isDefault == null)
            this.isDefault = false;
    }
}
