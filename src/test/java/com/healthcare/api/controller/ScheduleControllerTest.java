package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.ScheduleCreationRequest;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.ScheduleResponse;
import com.healthcare.api.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    ScheduleService scheduleService;

    private ScheduleCreationRequest creationRequest;
    private ScheduleResponse scheduleResponse;

    @BeforeEach
    void setUp() {
        creationRequest = new ScheduleCreationRequest();
        creationRequest.setAvailableFrom(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0));
        creationRequest.setAvailableTo(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0));
        creationRequest.setDurationTime(30);

        scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(UUID.randomUUID());
        scheduleResponse.setAvailableFrom(creationRequest.getAvailableFrom());
        scheduleResponse.setAvailableTo(creationRequest.getAvailableTo());
    }

    @Test
    void createSchedule_Success_Returns201Created() throws Exception {
        doNothing().when(scheduleService).createSchedule(creationRequest);

        mockMvc.perform(post("/api/v1/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(SuccessCode.CREATED.getCode()))
                .andExpect(jsonPath("$.message").value("Schedule created successfully"))
                .andExpect(jsonPath("$.result").value("Time slots saved to the system"));
    }

    @Test
    void createSchedule_ValidationFail_Returns400BadRequest() throws Exception {
        ScheduleCreationRequest request = new ScheduleCreationRequest();
        request.setAvailableFrom(null);

        mockMvc.perform(post("/api/v1/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1014))
                .andExpect(jsonPath("$.result.availableFrom").exists());
    }

    @Test
    void getMySchedules_Success_Returns200() throws Exception {
        PageResponse<ScheduleResponse> pageResponse = PageResponse.<ScheduleResponse>builder()
                .currentPage(1).pageSize(10).totalPages(1).totalElements(10)
                .data(List.of(scheduleResponse))
                .build();

        when(scheduleService.getMySchedules(anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/schedules/my-schedules")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.DATA_FETCHED.getCode()))
                .andExpect(jsonPath("$.message").value("Your work schedule list"))
                .andExpect(jsonPath("$.result.totalElements").value(10));
    }
}