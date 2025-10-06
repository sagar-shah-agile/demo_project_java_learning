package com.javatraining.notification_mgmt.model;

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
     * 📧 Email address of the recipient
     *//*
    @Column(nullable = false)
    private String recipientEmail;
*/
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
     * ✅ Whether the notification has already been sent
     * false = pending
     * true  = sent
     */
    private boolean sent = false;

    /**
     * 🔁 Retry attempts (useful for failed deliveries)
     */
    //private int retryCount = 0;

    // ✅ Link each notification to a registered user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
