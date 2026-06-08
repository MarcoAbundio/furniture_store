package com.furniturestore.employee.service;

import com.furniturestore.employee.dto.request.*;
import com.furniturestore.employee.dto.response.*;
import com.furniturestore.employee.exception.*;
import com.furniturestore.employee.mapper.EmployeeMapper;
import com.furniturestore.employee.model.*;
import com.furniturestore.employee.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock DepartmentRepository departmentRepository;
    @Mock PositionRepository positionRepository;
    @Mock EmployeeMapper employeeMapper;
    @InjectMocks EmployeeServiceImpl employeeService;

    private Department department;
    private Position position;
    private Employee employee;

    @BeforeEach
    void setUp() {
        department = Department.builder().id(1L).name("Ventas").isActive(true).build();
        position = Position.builder().id(1L).title("Cajero").department(department).isActive(true).build();
        employee = Employee.builder().id(1L).employeeCode("EMP-001").firstName("Juan")
                .lastName("García").email("juan@store.com").position(position)
                .salary(new BigDecimal("8500.00")).isActive(true).build();
    }

    @Test
    @DisplayName("createEmployee - should throw when employee code already exists")
    void createEmployee_codeExists_throws() {
        var req = EmployeeRequest.builder().employeeCode("EMP-001").email("new@store.com")
                .positionId(1L).hireDate(LocalDate.now()).salary(new BigDecimal("8000")).build();
        when(employeeRepository.existsByEmployeeCode("EMP-001")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee code already exists");
    }

    @Test
    @DisplayName("createEmployee - should create successfully")
    void createEmployee_success() {
        var req = EmployeeRequest.builder().employeeCode("EMP-002").firstName("Ana")
                .lastName("López").email("ana@store.com").positionId(1L)
                .hireDate(LocalDate.now()).salary(new BigDecimal("9000")).build();
        var expected = EmployeeResponse.builder().id(2L).employeeCode("EMP-002").build();

        when(employeeRepository.existsByEmployeeCode("EMP-002")).thenReturn(false);
        when(employeeRepository.existsByEmail("ana@store.com")).thenReturn(false);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toResponse(any())).thenReturn(expected);

        EmployeeResponse result = employeeService.createEmployee(req);
        assertThat(result).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("getEmployeeById - should throw ResourceNotFoundException when not found")
    void getEmployeeById_notFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deactivateEmployee - should set isActive to false")
    void deactivateEmployee_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);

        employeeService.deactivateEmployee(1L);

        assertThat(employee.getIsActive()).isFalse();
        verify(employeeRepository).save(employee);
    }

    @Test
    @DisplayName("createDepartment - should throw when name already exists")
    void createDepartment_nameExists_throws() {
        var req = new DepartmentRequest("Ventas", "desc");
        when(departmentRepository.existsByName("Ventas")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createDepartment(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Department already exists");
    }
}
