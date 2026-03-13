package com.healthcare.api.mapper;

import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.name", target = "roleName")
    UserResponse toUserResponse(User user);
}
