package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    private UserCreationRequest creationRequest;
    private UserResponse userResponse;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        creationRequest = new UserCreationRequest();
        creationRequest.setEmail("nguyen_doctor@gmail.com");
        creationRequest.setPassword("Password123");
        creationRequest.setFullName("Nguyen Doctor");
        creationRequest.setRoleId(2);

        userResponse = new UserResponse();
        userResponse.setId(UUID.randomUUID());
        userResponse.setEmail("nguyen_doctor@gmail.com");
        userResponse.setFullName("Nguyen Doctor");
        userResponse.setRoleName("ROLE_DOCTOR");
    }

    @Test
    void createUser_Success_Return201Created() throws Exception {
        when(userService.createUser(any())).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(SuccessCode.CREATED.getCode()))
                    .andExpect(jsonPath("$.message").value("User created successfully"))
                    .andExpect(jsonPath("$.result.email").value("nguyen_doctor@gmail.com"));
    }

    @Test
    void createUser_ValidationFail_Returns400BadRequest() throws Exception {
        creationRequest.setEmail("user#gmail,com");
        creationRequest.setFullName("");

        mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1014))
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.fullName").exists());
    }

    @Test
    void getUser_Success_Returns200Ok() throws Exception {
        when(userService.getUser(any())).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.DATA_FETCHED.getCode()))
                .andExpect(jsonPath("$.result.fullName").value("Nguyen Doctor"));
    }

    @Test
    void updateUser_ValidationFail_Returns400BadRequest() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFullName("");
        updateRequest.setPassword("");

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsers_Success_Returns200Ok() throws Exception {
        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .currentPage(1).pageSize(10).totalPages(1).totalElements(1)
                .data(List.of(userResponse))
                .build();

        when(userService.getUsers(anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/users")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.DATA_FETCHED.getCode()))
                .andExpect(jsonPath("$.result.totalElements").value(1));
    }

    @Test
    void updateUser_Success_Returns200Ok() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFullName("Nguyen Doctor");
        updateRequest.setPassword("Password123@");

        when(userService.updateUser(any(), any())).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Update user information by id"));
    }

    @Test
    void deleteUser_Success_Returns200Ok() throws Exception {
        doNothing().when(userService).deleteUser(any());

        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Delete user information by id"))
                .andExpect(jsonPath("$.result").value("User Id deleted: " + userId));
    }
}