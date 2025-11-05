package com.smartqueue.auth.controller;

import com.smartqueue.auth.dto.request.LogInRequest;
import com.smartqueue.auth.dto.request.RegistrationRequest;
import com.smartqueue.auth.dto.response.AuthLogInResponse;
import com.smartqueue.auth.dto.response.AuthRegistrationResponse;
import com.smartqueue.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PutMapping("/signUp")
    public ResponseEntity<AuthRegistrationResponse> signUp(@RequestBody @Valid RegistrationRequest request) {
        AuthRegistrationResponse response = authService.registration(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthLogInResponse> signIn(@RequestBody @Valid LogInRequest request) {
        AuthLogInResponse response = authService.logIn(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
