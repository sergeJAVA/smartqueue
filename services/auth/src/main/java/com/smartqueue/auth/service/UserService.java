package com.smartqueue.auth.service;

import com.smartqueue.auth.dto.UserDto;
import com.smartqueue.auth.dto.request.CreateUserRequest;
import com.smartqueue.auth.dto.request.SearchUsersRequest;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDto createUser(CreateUserRequest request);

    Boolean deleteUserById(Long id);

    Page<UserDto> findAll(SearchUsersRequest request);

}
