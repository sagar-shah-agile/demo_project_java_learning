package com.javatraining.notification_mgmt.model;

import com.javatraining.notification_mgmt.model.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 📝 Subject line of the email
     */
    private String subject;

    /**
     * 💬 Body/message of the email
     */
    @Column(length = 5000) // optional: allow longer messages
    private String message;

    /**
     * ⏰ When the notification should be sent
     */
    @Column(nullable = false)
    private LocalDateTime scheduledTime;


    /**
     * 🔁 Retry attempts (useful for failed deliveries)
     */
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "last_error", length = 1024)
    private String lastError;

    // ✅ Link each notification to a registered user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
