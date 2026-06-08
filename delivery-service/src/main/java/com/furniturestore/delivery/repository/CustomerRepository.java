package com.furniturestore.delivery.repository;

import com.furniturestore.delivery.model.Customer;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Customer> search(@Param("q") String query, Pageable pageable);
}
