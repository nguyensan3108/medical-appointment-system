package com.healthcare.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleCreationRequest {
    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime availableFrom;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime availableTo;

    private Integer durationTime = 30;
}
