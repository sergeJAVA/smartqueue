package com.smartqueue.auth.service;

import com.smartqueue.auth.dto.UserDto;
import com.smartqueue.auth.dto.request.CreateUserRequest;
import com.smartqueue.auth.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDto createUser(CreateUserRequest request);

    Boolean deleteUserById(Long id);

    Page<User> findAll();

}
