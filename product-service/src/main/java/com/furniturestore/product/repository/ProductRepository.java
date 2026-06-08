package com.furniturestore.product.repository;

import com.furniturestore.product.model.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.isActive = true")
    Page<Product> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.id = :categoryId AND p.isActive = true")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE " +
           "p.isActive = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> search(@Param("q") String query, Pageable pageable);
}
