package com.smartqueue.auth.dto.mapper;

import com.smartqueue.auth.dto.RoleDto;
import com.smartqueue.auth.entity.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleMapper {

    public static RoleDto toDto(Role role) {
        return RoleDto.builder()
                .name(role.getName())
                .build();
    }

}
