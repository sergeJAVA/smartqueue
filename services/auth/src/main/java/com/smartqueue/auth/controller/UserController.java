package com.smartqueue.auth.controller;

import com.smartqueue.auth.dto.PageDto;
import com.smartqueue.auth.dto.Pagination;
import com.smartqueue.auth.dto.UserDto;
import com.smartqueue.auth.dto.mapper.PageMapper;
import com.smartqueue.auth.dto.request.CreateUserRequest;
import com.smartqueue.auth.dto.request.SearchUsersRequest;
import com.smartqueue.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id) {
        Boolean response = userService.deleteUserById(id);
        if (response) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<PageDto<UserDto>> searchUsers(@RequestBody SearchUsersRequest request) {
        Pagination pagination = request.getPagination();
        pagination.setPage(Math.max(0, pagination.getPage()));
        pagination.setSize(Math.max(1, pagination.getSize()));
        Page<UserDto> users = userService.findAll(request);
        return ResponseEntity.ok(PageMapper.toDto(users));
    }

}
