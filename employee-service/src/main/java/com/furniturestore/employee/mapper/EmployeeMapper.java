package com.furniturestore.employee.mapper;

import com.furniturestore.employee.dto.response.*;
import com.furniturestore.employee.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "positionId", source = "position.id")
    @Mapping(target = "positionTitle", source = "position.title")
    @Mapping(target = "departmentId", source = "position.department.id")
    @Mapping(target = "departmentName", source = "position.department.name")
    @Mapping(target = "fullName", expression = "java(employee.getFirstName() + \" \" + employee.getLastName())")
    EmployeeResponse toResponse(Employee employee);

    DepartmentResponse toDepartmentResponse(Department department);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    PositionResponse toPositionResponse(Position position);
}
