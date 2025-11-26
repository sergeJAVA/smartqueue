package com.smartqueue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.auth.dto.request.LogInRequest;
import com.smartqueue.auth.dto.request.RegistrationRequest;
import com.smartqueue.auth.repository.UserRepository;
import com.smartqueue.auth.testUtils.TestContainer;
import com.smartqueue.auth.testUtils.TestMethods;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest extends TestContainer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static String baseURL;

    @BeforeAll
    static void setUp(){
        baseURL = "/api/v1/auth";
    }

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("signUp: Success.")
    void signUp_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Test", "pass");
        TestMethods.signUp(mockMvc, request, baseURL, objectMapper);
    }

    @Test
    @DisplayName("signIn: Success.")
    void signIn_Success() throws Exception {
        LogInRequest logInRequest = new LogInRequest("Test", "pass");
        RegistrationRequest registrationRequest = new RegistrationRequest("Test", "pass");

        TestMethods.signUp(mockMvc, registrationRequest, baseURL, objectMapper)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("User has been successfully registered"));

        TestMethods.signIn(mockMvc, logInRequest, baseURL, objectMapper)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("User has been authorized"));
    }

    @Test
    @DisplayName("signIn: Failure, wrong credentials.")
    void signIn_Failure() throws Exception {
        LogInRequest logInRequest = new LogInRequest("Test", "wrongPass");
        RegistrationRequest registrationRequest = new RegistrationRequest("Test", "pass");

        TestMethods.signUp(mockMvc, registrationRequest, baseURL, objectMapper)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("User has been successfully registered"));

        TestMethods.signIn(mockMvc, logInRequest, baseURL, objectMapper)
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("Incorrect username or password is specified"));

        logInRequest.setUsername("wrongName");
        logInRequest.setPassword("pass");
        TestMethods.signIn(mockMvc, logInRequest, baseURL, objectMapper)
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("Incorrect username or password is specified"));
    }

    @Test
    @DisplayName("signUp: Failure.")
    void signUp_Failure() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("Test", "pass");
        RegistrationRequest secondRegistrationRequest = new RegistrationRequest("Test", "password123");

        TestMethods.signUp(mockMvc, registrationRequest, baseURL, objectMapper)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("User has been successfully registered"));

        TestMethods.signUp(mockMvc, secondRegistrationRequest, baseURL, objectMapper)
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error")
                        .value("Username <<" + secondRegistrationRequest.getUsername() +
                                ">> is already taken!"));
    }


}