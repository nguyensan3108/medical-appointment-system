package com.healthcare.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.healthcare.api.validator.NotPastDate;
import com.healthcare.api.validator.ValidTimeRange;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ValidTimeRange
public class ScheduleCreationRequest {
    @NotNull(message = "Start time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotPastDate
    private LocalDateTime availableFrom;

    @NotNull(message = "End time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotPastDate
    private LocalDateTime availableTo;

    private Integer durationTime = 30;
}
