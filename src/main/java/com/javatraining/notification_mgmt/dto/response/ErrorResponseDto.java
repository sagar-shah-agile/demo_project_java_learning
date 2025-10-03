package com.javatraining.notification_mgmt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDto {

    @Schema(example = "2025-09-29T11:45:00", description = "Time when the error occurred")
    private LocalDateTime timestamp;

    @Schema(example = "404", description = "HTTP status code of the error")
    private int status;

    @Schema(example = "Not Found", description = "Reason phrase of the error")
    private String error;

    @Schema(example = "User not found with id: 99", description = "Detailed error message")
    private String message;

    @Schema(example = "/api/users/99", description = "Request path where the error occurred")
    private String path;

    /*@Schema(example = "a12b34cd-56ef-78gh-90ij-klmnopqrstuv", description = "Trace ID for debugging/log correlation")
    private String traceId; // <-- from MDC*/
}
