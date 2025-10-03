package com.javatraining.notification_mgmt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    @Schema(example = "1", description = "Unique ID of the user")
    private Long id;

    @Schema(example = "John Doe", description = "Full name of the user")
    private String name;

    @Schema(example = "john.doe@example.com", description = "User's email address")
    private String email;

    @Schema(example = "+91-9876543210", description = "User's phone number")
    private String phoneNumber;

    @Schema(example = "2025-09-29T11:30:45", description = "Timestamp when the user was created")
    private LocalDateTime createdAt;

    @Schema(example = "admin", description = "User who created the record")
    private String createdBy;

    @Schema(example = "2025-09-29T12:00:00", description = "Timestamp when the user was last updated")
    private LocalDateTime updatedAt;

    @Schema(example = "admin", description = "User who last updated the record")
    private String updatedBy;
}
