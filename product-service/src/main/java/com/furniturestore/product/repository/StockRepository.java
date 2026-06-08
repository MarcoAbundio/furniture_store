package com.furniturestore.product.repository;

import com.furniturestore.product.model.Stock;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductId(Long productId);

    @Query("SELECT s FROM Stock s JOIN FETCH s.product WHERE s.quantity <= s.minStock")
    List<Stock> findLowStock();

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity + :qty WHERE s.product.id = :productId")
    int incrementStock(@Param("productId") Long productId, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :qty WHERE s.product.id = :productId AND s.quantity >= :qty")
    int decrementStock(@Param("productId") Long productId, @Param("qty") int qty);
}
