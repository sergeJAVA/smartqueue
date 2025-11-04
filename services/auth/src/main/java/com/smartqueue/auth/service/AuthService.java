package com.smartqueue.auth.service;

import com.smartqueue.auth.dto.response.AuthLogInResponse;
import com.smartqueue.auth.dto.response.AuthRegistrationResponse;
import com.smartqueue.auth.dto.request.LogInRequest;
import com.smartqueue.auth.dto.request.RegistrationRequest;

public interface AuthService {

    AuthRegistrationResponse registration(RegistrationRequest request);

    AuthLogInResponse logIn(LogInRequest request);

}
