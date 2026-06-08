package com.furniturestore.employee.repository;

import com.furniturestore.employee.model.Position;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    @Query("SELECT p FROM Position p JOIN FETCH p.department WHERE p.isActive = true")
    Page<Position> findAllActive(Pageable pageable);
    @Query("SELECT p FROM Position p JOIN FETCH p.department d WHERE d.id = :deptId AND p.isActive = true")
    List<Position> findByDepartmentId(Long deptId);
}
