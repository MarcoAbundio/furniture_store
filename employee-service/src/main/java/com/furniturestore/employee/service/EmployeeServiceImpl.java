package com.furniturestore.employee.service;

import com.furniturestore.employee.dto.request.*;
import com.furniturestore.employee.dto.response.*;
import com.furniturestore.employee.exception.*;
import com.furniturestore.employee.mapper.EmployeeMapper;
import com.furniturestore.employee.model.*;
import com.furniturestore.employee.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeMapper employeeMapper;

    // ===== EMPLOYEE =====
    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest req) {
        if (employeeRepository.existsByEmployeeCode(req.getEmployeeCode()))
            throw new BusinessException("Employee code already exists: " + req.getEmployeeCode());
        if (employeeRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email already registered: " + req.getEmail());

        var position = positionRepository.findById(req.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + req.getPositionId()));

        var employee = Employee.builder()
                .employeeCode(req.getEmployeeCode()).firstName(req.getFirstName())
                .lastName(req.getLastName()).email(req.getEmail()).phone(req.getPhone())
                .position(position).userId(req.getUserId()).hireDate(req.getHireDate())
                .salary(req.getSalary()).isActive(true).build();

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeMapper.toResponse(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByCode(String code) {
        return employeeMapper.toResponse(employeeRepository.findByEmployeeCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + code)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAllActive(pageable).map(employeeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        if (!departmentRepository.existsById(departmentId))
            throw new ResourceNotFoundException("Department not found: " + departmentId);
        return employeeRepository.findByDepartmentId(departmentId, pageable).map(employeeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployees(String query, Pageable pageable) {
        return employeeRepository.search(query, pageable).map(employeeMapper::toResponse);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest req) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));

        if (!employee.getEmployeeCode().equals(req.getEmployeeCode())
                && employeeRepository.existsByEmployeeCode(req.getEmployeeCode()))
            throw new BusinessException("Employee code already in use: " + req.getEmployeeCode());

        if (!employee.getEmail().equals(req.getEmail()) && employeeRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email already registered: " + req.getEmail());

        var position = positionRepository.findById(req.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + req.getPositionId()));

        employee.setEmployeeCode(req.getEmployeeCode()); employee.setFirstName(req.getFirstName());
        employee.setLastName(req.getLastName()); employee.setEmail(req.getEmail());
        employee.setPhone(req.getPhone()); employee.setPosition(position);
        employee.setUserId(req.getUserId()); employee.setHireDate(req.getHireDate());
        employee.setSalary(req.getSalary());

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void deactivateEmployee(Long id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    // ===== DEPARTMENT =====
    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest req) {
        if (departmentRepository.existsByName(req.getName()))
            throw new BusinessException("Department already exists: " + req.getName());
        var dept = Department.builder().name(req.getName()).description(req.getDescription()).isActive(true).build();
        return employeeMapper.toDepartmentResponse(departmentRepository.save(dept));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        return employeeMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllActive(pageable).map(employeeMapper::toDepartmentResponse);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest req) {
        var dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
        dept.setName(req.getName()); dept.setDescription(req.getDescription());
        return employeeMapper.toDepartmentResponse(departmentRepository.save(dept));
    }

    // ===== POSITION =====
    @Override
    @Transactional
    public PositionResponse createPosition(PositionRequest req) {
        var dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + req.getDepartmentId()));
        var pos = Position.builder().title(req.getTitle()).department(dept).baseSalary(req.getBaseSalary()).isActive(true).build();
        return employeeMapper.toPositionResponse(positionRepository.save(pos));
    }

    @Override
    @Transactional(readOnly = true)
    public PositionResponse getPositionById(Long id) {
        return employeeMapper.toPositionResponse(positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PositionResponse> getAllPositions(Pageable pageable) {
        return positionRepository.findAllActive(pageable).map(employeeMapper::toPositionResponse);
    }

    @Override
    @Transactional
    public PositionResponse updatePosition(Long id, PositionRequest req) {
        var pos = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + id));
        var dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + req.getDepartmentId()));
        pos.setTitle(req.getTitle()); pos.setDepartment(dept); pos.setBaseSalary(req.getBaseSalary());
        return employeeMapper.toPositionResponse(positionRepository.save(pos));
    }
}
