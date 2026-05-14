package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.AuthenticationRequest;
import com.healthcare.api.dto.response.AuthenticationResponse;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    private AuthenticationRequest authRequest;
    private AuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {
        authRequest = AuthenticationRequest.builder()
                .email("admin@gmail.com")
                .password("Password123")
                .build();

        authResponse = AuthenticationResponse.builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mocked_token_string")
                .authenticated(true)
                .build();
    }

    @Test
    void authenticate_Success_Returns200Ok() throws Exception {
        when(authenticationService.authenticate(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.result.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mocked_token_string"))
                .andExpect(jsonPath("$.result.authenticated").value(true));
    }

    @Test
    void authenticate_ValidationFail_Returns401Unauthorized() throws Exception {
        when(authenticationService.authenticate(any()))
                .thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        mockMvc.perform(post("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHENTICATED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHENTICATED.getMessage()));
    }
}