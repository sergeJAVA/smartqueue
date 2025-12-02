package com.smartqueue.auth.dto.mapper;

import com.smartqueue.auth.dto.UserDto;
import com.smartqueue.auth.entity.User;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles()
                        .stream()
                        .map(RoleMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }

}
