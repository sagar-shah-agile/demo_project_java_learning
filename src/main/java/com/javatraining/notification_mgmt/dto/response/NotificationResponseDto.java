package com.javatraining.notification_mgmt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;
    private String subject;
    private String message;
    private LocalDateTime scheduledTime;
    private boolean sent;
    private String recipientEmail; // from user.getEmail()
}
