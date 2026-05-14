package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.service.AppointmentService;
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

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AppointmentService appointmentService;

    private AppointmentCreationRequest creationRequest;
    private AppointmentResponse appointmentResponse;
    private String appointmentId;
    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID().toString();

        creationRequest = new AppointmentCreationRequest();
        creationRequest.setScheduleId(UUID.randomUUID());
        creationRequest.setReason("test");

        appointmentResponse = new AppointmentResponse();
        appointmentResponse.setId(UUID.fromString(appointmentId));
        appointmentResponse.setReason("test");
        appointmentResponse.setStatus("PENDING");
    }

    @Test
    void bookAppointment_Success_Returns201Created() throws Exception {
        when(appointmentService.bookAppointment(any())).thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(SuccessCode.CREATED.getCode()))
                .andExpect(jsonPath("$.message").value("Appointment booked successfully"))
                .andExpect(jsonPath("$.result.reason").value("test"));
    }

    @Test
    void bookAppointment_ValidationFail_Returns404BadRequest() throws Exception {
        AppointmentCreationRequest creationRequest = new AppointmentCreationRequest();
        creationRequest.setScheduleId(UUID.randomUUID());
        creationRequest.setReason("");

        when(appointmentService.bookAppointment(any())).thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1014))
                .andExpect(jsonPath("$.result.reason").exists());
    }

    @Test
    void cancelAppointment_Success_Returns200Ok() throws Exception {
        doNothing().when(appointmentService).cancelAppointment(any());

        mockMvc.perform(put("/api/v1/appointments/{appointmentId}/cancel", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Appointment has been cancelled successfully"));
    }

    @Test
    void getMyAppointments_Success_Returns200Ok() throws Exception {
        PageResponse<AppointmentResponse> pageResponse = PageResponse.<AppointmentResponse>builder()
                .currentPage(1).pageSize(10).totalPages(1).totalElements(1)
                .data(List.of(appointmentResponse))
                .build();

        when(appointmentService.getMyAppointments(anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/appointments/my-appointments")
                .param("page","1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Fetched appointments successfully"))
                .andExpect(jsonPath("$.result.totalElements").value(1));
    }
}