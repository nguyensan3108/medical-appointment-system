package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.ScheduleCreationRequest;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.ScheduleResponse;
import com.healthcare.api.entity.Doctor;
import com.healthcare.api.entity.Schedule;
import com.healthcare.api.entity.User;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.ScheduleMapper;
import com.healthcare.api.repository.DoctorRepository;
import com.healthcare.api.repository.ScheduleRepository;
import com.healthcare.api.repository.UserRepository;
import com.healthcare.api.utils.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleMapper scheduleMapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleServiceImpl;

    @Captor
    private ArgumentCaptor<List<Schedule>> scheduleListCaptor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleCreationRequest request;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2030, 5, 1, 8, 0);
        endTime = LocalDateTime.of(2030, 5, 1, 9, 0);

        request = new ScheduleCreationRequest();
        request.setAvailableFrom(startTime);
        request.setAvailableTo(endTime);
        request.setDurationTime(30);
    }

    @Test
    void createSchedule_Success() {
        User doctorUser  = new User();
        doctorUser.setId(UUID.randomUUID());
        doctorUser.setEmail("doctor@gmail.com");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setUser(doctorUser);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndAvailableFromBetween(any(), any(), any())).thenReturn(new ArrayList<>());

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(doctorUser.getEmail());

            scheduleServiceImpl.createSchedule(request);

            verify(scheduleRepository,  times(1)).saveAll(scheduleListCaptor.capture());

            List<Schedule> capturedSchedules = scheduleListCaptor.getValue();

            assertEquals(2, capturedSchedules.size());

            Schedule firstSlot = capturedSchedules.get(0);
            assertEquals(startTime, firstSlot.getAvailableFrom());
            assertEquals(startTime.plusMinutes(30), firstSlot.getAvailableTo());
            assertTrue(firstSlot.isAvailable());

            Schedule secondSlot = capturedSchedules.get(1);
            assertEquals(startTime.plusMinutes(30), secondSlot.getAvailableFrom());
            assertEquals(endTime, secondSlot.getAvailableTo());
            assertTrue(secondSlot.isAvailable());
        }
    }

    @Test
    void createSchedule_DoctorNotFound() {
        User doctorUser = new User();
        doctorUser.setId(UUID.randomUUID());
        doctorUser.setEmail("anonymous@gmail.com");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.empty());

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(doctorUser.getEmail());

            AppException exception = assertThrows(AppException.class, () -> scheduleServiceImpl.createSchedule(request));

            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

            verify(scheduleRepository,  never()).findByDoctorIdAndAvailableFromBetween(any(), any(), any());
            verify(scheduleRepository,  never()).save(any());
        }
    }

    @Test
    void createSchedule_ScheduleAlreadyBooked() {
        User doctorUser = new User();
        doctorUser.setId(UUID.randomUUID());
        doctorUser.setEmail("doctor@gmail.com");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setUser(doctorUser);

        Schedule existingSlot1 = new Schedule();
        existingSlot1.setAvailableFrom(startTime);

        Schedule existingSlot2 = new Schedule();
        existingSlot2.setAvailableFrom(startTime.plusMinutes(30));

        List<Schedule> existingSchedules = new ArrayList<>();
        existingSchedules.add(existingSlot1);
        existingSchedules.add(existingSlot2);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndAvailableFromBetween(any(), any(), any())).thenReturn(existingSchedules);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(doctorUser.getEmail());

            AppException exception = assertThrows(AppException.class, () -> scheduleServiceImpl.createSchedule(request));

            assertEquals(ErrorCode.SCHEDULE_ALREADY_BOOKED, exception.getErrorCode());
            verify(scheduleRepository,  never()).save(any());
        }
    }

    @Test
    void getMySchedules_Success() {
        User doctorUser = new User();
        doctorUser.setId(UUID.randomUUID());
        doctorUser.setEmail("doctor@gmail.com");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setUser(doctorUser);

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        Schedule schedule = new Schedule();
        List<Schedule> scheduleList = new ArrayList<>();
        scheduleList.add(schedule);

        Page<Schedule> schedulePage = new PageImpl(scheduleList, pageable, 1);

        ScheduleResponse scheduleResponse = new ScheduleResponse();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndAvailableFromBetween(
                eq(doctor.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable))
        ).thenReturn(schedulePage);
        when(scheduleMapper.toScheduleResponse(any())).thenReturn(scheduleResponse);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(doctorUser.getEmail());

            PageResponse<ScheduleResponse> result = scheduleServiceImpl.getMySchedules(page, size);

            assertNotNull(result);
            assertEquals(1, result.getCurrentPage());
            assertEquals(1, result.getTotalPages());
            assertEquals(size, result.getPageSize());
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getData().size());
            assertEquals(scheduleResponse, result.getData().get(0));
        }
    }

    @Test
    void createSchedule_UserNotFound() {
        String email = "uknown@gmail.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(email);

            AppException exception = assertThrows(AppException.class, () ->
                    scheduleServiceImpl.createSchedule(request));

            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

            verify(doctorRepository, never()).findByUserId(any());
            verify(scheduleRepository, never()).findByDoctorIdAndAvailableFromBetween(any(), any(), any());
            verify(scheduleRepository, never()).saveAll(any());
        }
    }

    @Test
    void createSchedule_PartialSuccess() {
        User doctorUser  = new User();
        doctorUser.setId(UUID.randomUUID());
        doctorUser.setEmail("doctor@gmail.com");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setUser(doctorUser);

        Schedule existingSlot = new Schedule();
        existingSlot.setAvailableFrom(startTime);

        List<Schedule> existingSchedules = new ArrayList<>();
        existingSchedules.add(existingSlot);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndAvailableFromBetween(any(), any(), any()))
                .thenReturn(existingSchedules);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(doctorUser.getEmail());
            scheduleServiceImpl.createSchedule(request);

            verify(scheduleRepository, times(1)).saveAll(scheduleListCaptor.capture());
            List<Schedule> capturedSchedules = scheduleListCaptor.getValue();

            assertEquals(1, capturedSchedules.size());

            Schedule savedSlot = capturedSchedules.get(0);
            assertEquals(startTime.plusMinutes(30), savedSlot.getAvailableFrom());
            assertEquals(endTime, savedSlot.getAvailableTo());
            assertTrue(savedSlot.isAvailable());
        }
    }

    @Test
    void getMySchedules_EmptyList() {
        User doctorUser  = new User();
        doctorUser.setId(UUID.randomUUID());
        doctorUser.setEmail("doctor@gmail.com");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setUser(doctorUser);

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);
        PageImpl<Schedule> emptyList = new PageImpl(new ArrayList<>(), pageable, 0);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndAvailableFromBetween(any(), any(), any(), any())).thenReturn(emptyList);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(doctorUser.getEmail());
            PageResponse<ScheduleResponse> result = scheduleServiceImpl.getMySchedules(page, size);
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getData().isEmpty());
        }
    }
}