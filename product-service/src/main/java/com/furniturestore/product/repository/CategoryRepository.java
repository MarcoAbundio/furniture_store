package com.furniturestore.product.repository;

import com.furniturestore.product.model.Category;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parent IS NULL")
    Page<Category> findAllRootCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    Page<Category> findAllActive(Pageable pageable);
}
