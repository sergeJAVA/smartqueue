package com.smartqueue.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.auth.dto.PageDto;
import com.smartqueue.auth.dto.Pagination;
import com.smartqueue.auth.dto.UserDto;
import com.smartqueue.auth.dto.request.CreateUserRequest;
import com.smartqueue.auth.dto.request.SearchUsersRequest;
import com.smartqueue.auth.repository.UserRepository;
import com.smartqueue.auth.security.CustomUserDetails;
import com.smartqueue.auth.security.service.JwtService;
import com.smartqueue.auth.service.UserService;
import com.smartqueue.auth.testUtils.TestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class UserControllerTest extends TestContainer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private String userToken;

    private String adminToken;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();

        CustomUserDetails user = new CustomUserDetails(
                11L,
                "user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_OPERATOR")));

        CustomUserDetails admin = new CustomUserDetails(
                22L,
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        userToken = "Bearer " + jwtService.generateJwtToken(user);
        adminToken = "Bearer " + jwtService.generateJwtToken(admin);
    }

    @Test
    void createUser_Success() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .password("password")
                .roles(List.of("operator", "user"))
                .build();

        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.roles.length()").value(2))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("USER", "OPERATOR")));
    }

    @Test
    void createUser_Failure_NoRequiredRole() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .password("password")
                .roles(List.of("operator", "user"))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", userToken))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message")
                        .value("You do not have sufficient permissions to access this resource. Required role: ADMIN."));
    }

    @Test
    void deleteUserById_Success() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("user")
                .password("password")
                .roles(List.of("operator", "user"))
                .build();

        String response = mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .header("Authorization", adminToken))
                .andReturn().getResponse().getContentAsString();

        UserDto userDto = objectMapper.readValue(response, UserDto.class);

        mockMvc.perform(delete("/api/v1/user/" + userDto.getId())
                .header("Authorization", adminToken))
                .andExpect(status().is(204));

        mockMvc.perform(delete("/api/v1/user/" + userDto.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().is(404));
    }

    @Test
    void searchUsers() throws Exception {
        CreateUserRequest createUser = CreateUserRequest.builder()
                .username("user")
                .password("password")
                .roles(List.of("user"))
                .build();

        CreateUserRequest createAdmin = CreateUserRequest.builder()
                .username("admin")
                .password("password")
                .roles(List.of("admin"))
                .build();

        CreateUserRequest createOperator = CreateUserRequest.builder()
                .username("operator")
                .password("password")
                .roles(List.of("operator"))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUser))
                        .header("Authorization", adminToken))
                        .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAdmin))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOperator))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        SearchUsersRequest request =
                new SearchUsersRequest(null, List.of("admin", "user"), new Pagination(5, 0));
        PageDto<UserDto> response = objectMapper.readValue(mockMvc.perform(post("/api/v1/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<PageDto<UserDto>>() {});
        assertEquals(2, response.getContent().size());

        request = new SearchUsersRequest(null, List.of("admin"), new Pagination(5, 0));
        response = objectMapper.readValue(mockMvc.perform(post("/api/v1/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<PageDto<UserDto>>() {});
        assertEquals(1, response.getContent().size());

        request = new SearchUsersRequest(null, List.of("OpeRator"), new Pagination(5, 0));
        response = objectMapper.readValue(mockMvc.perform(post("/api/v1/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<PageDto<UserDto>>() {});
        assertEquals(1, response.getContent().size());
        assertThat(response.getContent().getFirst().getRoles().iterator().next().getName()).isEqualTo("OPERATOR");

        request = new SearchUsersRequest(null, List.of("user"), new Pagination(5, 0));
        mockMvc.perform(post("/api/v1/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("user"))
                .andExpect(jsonPath("$.content[0].roles[*].name", hasItem("USER")))
                .andExpect(jsonPath("$.content[0].roles.length()").value(1));

        request = new SearchUsersRequest(null, null, new Pagination(5, 0));
        mockMvc.perform(post("/api/v1/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[*].roles[*].name", containsInAnyOrder("USER", "OPERATOR", "ADMIN")));
    }

}