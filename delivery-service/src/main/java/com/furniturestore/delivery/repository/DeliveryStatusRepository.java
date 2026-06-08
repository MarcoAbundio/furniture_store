package com.furniturestore.delivery.repository;

import com.furniturestore.delivery.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {
    Optional<DeliveryStatus> findByCode(String code);
}
