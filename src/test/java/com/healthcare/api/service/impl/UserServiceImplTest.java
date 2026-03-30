package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.entity.Doctor;
import com.healthcare.api.entity.Patient;
import com.healthcare.api.entity.Role;
import com.healthcare.api.entity.User;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.DoctorMapper;
import com.healthcare.api.mapper.PatientMapper;
import com.healthcare.api.mapper.UserMapper;
import com.healthcare.api.repository.DoctorRepository;
import com.healthcare.api.repository.PatientRepository;
import com.healthcare.api.repository.RoleRepository;
import com.healthcare.api.repository.UserRepository;
import com.healthcare.api.utils.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreationRequest request;

    @BeforeEach
    void setUp() {
        request = new UserCreationRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("Password123");
        request.setFullName("Jackie Chan");
        request.setRoleId(1);
    }

    @Test
    void createUser_WhenEmailExists_ThrowsAppException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            userService.createUser(request);
        });
        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
    }

    @Test
    void createUser_RoleNotFound_ThrowsAppException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById(anyInt())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () ->
            userService.createUser(request));

            assertEquals(ErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createUser_PatientSuccess_ReturnUserResponse() {
        Role patientRole = new Role();
        patientRole.setName("PATIENT");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(request.getEmail());
        savedUser.setRole(patientRole);

        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setEmail(savedUser.getEmail());

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById(any())).thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(patientMapper.toPatient(any())).thenReturn(new Patient());
        when(userMapper.toUserResponse(any())).thenReturn(expectedUserResponse);
        when(patientRepository.findByUserId(any())).thenReturn(empty());

        var result = userService.createUser(request);
        assertEquals(expectedUserResponse.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(patientRepository, times(1)).save(any());
    }

    @Test
    void createUser_DoctorSuccess_ReturnUserResponse() {
        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(request.getEmail());
        savedUser.setRole(doctorRole);

        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setEmail(savedUser.getEmail());

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById(any())).thenReturn(Optional.of(doctorRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(doctorMapper.toDoctor(any())).thenReturn(new Doctor());
        when(userMapper.toUserResponse(any())).thenReturn(expectedUserResponse);
        when(doctorRepository.findByUserId(any())).thenReturn(empty());

        var result = userService.createUser(request);
        assertEquals(expectedUserResponse.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(doctorRepository, times(1)).save(any());
    }

    @Test
    void getMyInfo_Success_ReturnUserResponse() {
        String email = "test_email@gmail.com";
        User user = new User();
        user.setEmail(email);
        user.setId(UUID.randomUUID());
        Role role = new Role();
        role.setName("PATIENT");
        user.setRole(role);

        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setEmail(email);

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toUserResponse(any())).thenReturn(expectedUserResponse);
            when(patientRepository.findByUserId(any())).thenReturn(Optional.empty());

            var result = userService.getMyInfo();
            assertEquals(email, result.getEmail());
        }
    }

    @Test
    void getMyInfo_UserNotFound_ThrowsAppException() {
        String email = "uknown@email.com";

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(email);

            when(userRepository.findByEmail(email)).thenReturn(empty());

            AppException exception = assertThrows(AppException.class, () ->
                userService.getMyInfo());
                assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Test
    void getUser_Success_ReturnUserResponse() {
        UUID id = UUID.randomUUID();
        Role role = new Role();
        role.setName("PATIENT");
        User user = new User();
        user.setId(id);
        user.setEmail("user_email@gmail.com");
        user.setRole(role);

        UserResponse response = new UserResponse();
        response.setEmail("user_email@gmail.com");

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(response);
        when(patientRepository.findByUserId(any())).thenReturn(empty());

        var result = userService.getUser(id.toString());

        assertEquals("user_email@gmail.com", result.getEmail());
    }

    @Test
    void getUser_NotFound_ThrowsAppException() {
        when(userRepository.findById(any())).thenReturn(empty());

        AppException exception = assertThrows(AppException.class, () ->
                userService.getUser(UUID.randomUUID().toString()));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateUser_Success_ReturnUserResponse() {
        UUID id = UUID.randomUUID();
        var updateRequest = new UserUpdateRequest();
        updateRequest.setPassword("NEW_PASSWORD");
        updateRequest.setFullName("NEW_FULL_NAME");
        Role role = new Role();
        role.setName("PATIENT");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setRole(role);

        User savedUSer = new User();
        savedUSer.setFullName("NEW_FULL_NAME");
        savedUSer.setRole(role);

        UserResponse response = new UserResponse();
        response.setFullName("NEW_FULL_NAME");

        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(savedUSer);
        when(userMapper.toUserResponse(any())).thenReturn(response);

        var result = userService.updateUser(id.toString(), updateRequest);

        assertEquals("NEW_FULL_NAME", result.getFullName());
        verify(userRepository).save(any());
    }

    @Test
    void deleteUser_Success() {
        UUID id = UUID.randomUUID();
        String stringId = id.toString();

        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(stringId);

        verify(userRepository, times(1)).deleteById(any(UUID.class));
    }
}