package com.furniturestore.auth.mapper;

import com.furniturestore.auth.dto.response.*;
import com.furniturestore.auth.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.name")
    UserResponse toResponse(User user);

    RoleResponse toRoleResponse(Role role);
}
