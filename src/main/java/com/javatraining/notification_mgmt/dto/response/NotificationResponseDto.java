package com.javatraining.notification_mgmt.dto.response;

import com.javatraining.notification_mgmt.model.enums.NotificationStatus;
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
    private NotificationStatus status;      // 🆕 replaces 'sent'
    private int retryCount;                 // 🆕 show retry attempts
    private String lastError;               // 🆕 useful for debugging
    private LocalDateTime lastAttemptAt;    // 🆕 last attempt time
    private String recipientEmail;          // from user.getEmail()
}
