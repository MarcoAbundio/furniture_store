package com.furniturestore.employee.repository;

import com.furniturestore.employee.model.Employee;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmployeeCode(String code);
    boolean existsByEmail(String email);
    Optional<Employee> findByEmployeeCode(String code);

    @Query("SELECT e FROM Employee e JOIN FETCH e.position p JOIN FETCH p.department WHERE e.isActive = true")
    Page<Employee> findAllActive(Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN FETCH e.position p JOIN FETCH p.department d WHERE d.id = :deptId AND e.isActive = true")
    Page<Employee> findByDepartmentId(@Param("deptId") Long deptId, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN FETCH e.position p JOIN FETCH p.department WHERE e.isActive = true AND " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Employee> search(@Param("q") String query, Pageable pageable);
}
