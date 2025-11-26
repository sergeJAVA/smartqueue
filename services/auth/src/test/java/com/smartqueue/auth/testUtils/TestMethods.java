package com.smartqueue.auth.testUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.auth.dto.request.LogInRequest;
import com.smartqueue.auth.dto.request.RegistrationRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TestMethods {

    public static ResultActions signUp(MockMvc mockMvc,
                                       RegistrationRequest request,
                                       String baseURL,
                                       ObjectMapper mapper) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.put(baseURL + "/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));
    }

    public static ResultActions signIn(MockMvc mockMvc,
                              LogInRequest request,
                              String baseURL,
                              ObjectMapper mapper) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post(baseURL + "/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));
    }


}
