package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.MedicalRecordCreationRequest;
import com.healthcare.api.dto.response.MedicalRecordResponse;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MedicalRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class MedicalRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    private MedicalRecordCreationRequest creationRequest;
    private MedicalRecordResponse recordResponse;
    private String appointmentId;
    private String recordId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID().toString();
        recordId = UUID.randomUUID().toString();

        creationRequest = new MedicalRecordCreationRequest();
        creationRequest.setAppointmentId(appointmentId);
        creationRequest.setDiagnosis("Diagnosis");
        creationRequest.setTreatmentPlan("TreatmentPlan");

        recordResponse = new MedicalRecordResponse();
        recordResponse.setId(recordId);
        recordResponse.setAppointmentId(appointmentId);
        recordResponse.setDiagnosis("Diagnosis");
        recordResponse.setTreatmentPlan("TreatmentPlan");
    }

    @Test
    void createMedicalRecord_Success_Returns201Created() throws Exception {
        when(medicalRecordService.createMedicalRecord(any())).thenReturn(recordResponse);

        mockMvc.perform(post("/api/v1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(SuccessCode.CREATED.getCode()))
                .andExpect(jsonPath("$.message").value("Medical record created successfully"))
                .andExpect(jsonPath("$.result.diagnosis").value("Diagnosis"));
    }

    @Test
    void createMedicalRecord_ValidationFail_Returns400BadRequest() throws Exception {
        MedicalRecordCreationRequest badRequest = new MedicalRecordCreationRequest();
        badRequest.setDiagnosis("");

        mockMvc.perform(post("/api/v1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("$.result.diagnosis").exists());
    }

    @Test
    void getMedicalRecordByAppointmentId_Success_Returns200Ok() throws Exception {
        when(medicalRecordService.getMedicalRecordByAppointmentId(anyString())).thenReturn(recordResponse);

        mockMvc.perform(get("/api/v1/medical-records/appointment/{appointmentId}",appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.DATA_FETCHED.getCode()))
                .andExpect(jsonPath("$.message").value("Medical record retrieved successfully"))
                .andExpect(jsonPath("$.result.appointmentId").value(appointmentId));
    }
}