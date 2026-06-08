package com.furniturestore.employee.service;

import com.furniturestore.employee.dto.request.*;
import com.furniturestore.employee.dto.response.*;
import org.springframework.data.domain.*;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByCode(String code);
    Page<EmployeeResponse> getAllEmployees(Pageable pageable);
    Page<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable);
    Page<EmployeeResponse> searchEmployees(String query, Pageable pageable);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deactivateEmployee(Long id);

    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse getDepartmentById(Long id);
    Page<DepartmentResponse> getAllDepartments(Pageable pageable);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);

    PositionResponse createPosition(PositionRequest request);
    PositionResponse getPositionById(Long id);
    Page<PositionResponse> getAllPositions(Pageable pageable);
    PositionResponse updatePosition(Long id, PositionRequest request);
}
