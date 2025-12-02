package com.smartqueue.auth.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {

    @NotEmpty(message = "The username must not be empty!")
    @Size(min = 4, message = "The username must be at least 4 characters long")
    private String username;

    @NotEmpty(message = "The password must not be empty!")
    @Size(min = 4, message = "The password must be at least 4 characters long")
    private String password;

    private List<String> roles;

}
