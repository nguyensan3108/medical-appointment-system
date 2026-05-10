package com.healthcare.api.service;

import com.healthcare.api.dto.request.AuthenticationRequest;
import com.healthcare.api.entity.Role;
import com.healthcare.api.entity.User;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationService authenticationService;
    private AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        request = new AuthenticationRequest();
        request.setEmail("user@gmail.com");
        request.setPassword("Password123");
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", "12345678123456781234567812345678");
    }

    @Test
    void AuthenticateUser_Success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@gmail.com");
        user.setPassword("Password123");

        Role role = new Role();
        role.setName("PATIENT");
        user.setRole(role);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        var result = authenticationService.authenticate(request);

        assertNotNull(result.getToken());
        assertTrue(result.isAuthenticated());
    }

    @Test
    void AuthenticateUser_Fail_WrongPassword() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@gmail.com");
        user.setPassword("Password123");

        Role role = new Role();
        role.setName("PATIENT");
        user.setRole(role);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.authenticate(request);
        });
        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void AuthenticateUser_Fail_UserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());


        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.authenticate(request);
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}