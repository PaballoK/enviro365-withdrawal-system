package com.enviro.assessment.junior.paballo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {

    private int code;
    private String status;
    private String message;
    private LocalDateTime timestamp;
}
