package com.furniturestore.employee.repository;

import com.furniturestore.employee.model.Department;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
    @Query("SELECT d FROM Department d WHERE d.isActive = true")
    Page<Department> findAllActive(Pageable pageable);
}
