package com.furniturestore.delivery.repository;

import com.furniturestore.delivery.model.Delivery;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByDeliveryNumber(String deliveryNumber);
    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    @Query("SELECT d FROM Delivery d JOIN FETCH d.customer JOIN FETCH d.address JOIN FETCH d.status WHERE d.customer.id = :customerId")
    Page<Delivery> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT d FROM Delivery d JOIN FETCH d.customer JOIN FETCH d.address JOIN FETCH d.status WHERE d.employeeId = :empId")
    Page<Delivery> findByEmployeeId(@Param("empId") Long employeeId, Pageable pageable);

    @Query("SELECT d FROM Delivery d JOIN FETCH d.customer JOIN FETCH d.address JOIN FETCH d.status WHERE d.status.code = :code")
    Page<Delivery> findByStatusCode(@Param("code") String code, Pageable pageable);

    @Query("SELECT d FROM Delivery d JOIN FETCH d.customer JOIN FETCH d.address JOIN FETCH d.status " +
           "WHERE d.scheduledDate BETWEEN :from AND :to")
    Page<Delivery> findByScheduledDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);

    @Query("SELECT d FROM Delivery d JOIN FETCH d.customer JOIN FETCH d.address JOIN FETCH d.status")
    Page<Delivery> findAllWithDetails(Pageable pageable);
}
