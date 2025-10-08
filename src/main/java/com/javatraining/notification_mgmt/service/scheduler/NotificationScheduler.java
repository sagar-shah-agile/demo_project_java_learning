package com.javatraining.notification_mgmt.service.scheduler;

import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.model.User;
import com.javatraining.notification_mgmt.model.enums.NotificationStatus;
import com.javatraining.notification_mgmt.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//🧠 How it works (Notification Scheduler Flow)
//----------------------------------------------
//1️⃣ Scheduler Trigger
//   → Runs every 5 minutes (cron: "0 */5 * * * *")
//        → Automatically scans for due notifications.
//
//2️⃣ Fetch Pending Notifications
//   → Finds notifications with:
//        • status = PENDING or RETRYING
//        • scheduledTime <= now
//        • retryCount < maxRetries
//
//3️⃣ Attempt Email Delivery
//   → For each notification:
//        • Sends email using JavaMailSender
//        • On success → mark as SENT and clear errors
//
//4️⃣ Failure Handling
//   → On failure:
//        • retryCount = retryCount + 1
//        • If retryCount < maxRetries:
//        - status = RETRYING
//              - scheduledTime = now + (backoffMinutes × retryCount)
//        - save & retry later
//        • Else if retryCount ≥ maxRetries:
//        - status = FAILED (permanent failure)
//              - stop further attempts
//
//5️⃣ Logging & Persistence
//   → Each step is logged:
//        • Success → “✅ SENT”
//        • Retry scheduled → “⏳ RETRYING”
//        • Permanent failure → “🚫 FAILED”
//        ----------------------------------------------

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    // from properties
    @Value("${notification.max-retries}")
    private int maxRetries;

    @Value("${notification.backoff-minutes}")
    private int backoffMinutes;

    /**
     * 🕒 Scheduled Task — runs every 5 minutes
     *
     * ▶ Fetches notifications that are due (PENDING/RETRYING)
     * ▶ Sends emails and updates their status accordingly:
     *    - ✅ Success → mark SENT
     *    - ⚠️ Failure → retry with exponential backoff
     *    - 🚫 Max retries reached → mark FAILED
     *
     * Ensures all notification lifecycle transitions are handled automatically.
     */
    //@Scheduled(cron = "0 * * * * *")  // runs every 1 minute at 0 sec
    //@Scheduled(fixedDelay = 300000) // 5 minutes after last execution ends
    @Scheduled(cron = "0 */5 * * * *") // every 5 minutes
    @Transactional(readOnly = false)
    public void processDueNotifications() {
        log.info("🔍 Checking for due notifications at {}", LocalDateTime.now());

        // 1️⃣  Fetch pending/retrying notifications due for sending
        List<Notification> dueNotifications =
                notificationRepository.findPendingNotificationsWithRetry(LocalDateTime.now(), maxRetries);
        
        if (dueNotifications.isEmpty()) {
            log.info("✅ No notifications due at this time.");
            return;
        }

        // 2️⃣ Loop through each due notification and try to send it
        for (Notification notification : dueNotifications) {
            try {
                User user = notification.getUser();
                if (user == null || user.getEmail() == null) {
                    log.warn("⚠️ Skipping notification ID={} — user or email missing", notification.getId());
                    continue;
                }

                sendEmail(notification);

                // 3️⃣ Successful send ✅ Update status in DB
                notification.setStatus(NotificationStatus.SENT);
                notification.setRetryCount(notification.getRetryCount()); // keep as is
                notification.setLastAttemptAt(LocalDateTime.now());
                notification.setLastError(null);
                notificationRepository.saveAndFlush(notification);

                // 🧾 Log detailed info
                log.info("📧 Notification ID={} | User ID={} | Name={} | Email={} | Subject='{}' ✅ SENT",
                        notification.getId(),
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        notification.getSubject()
                );

            } catch (Exception e) {
                handleFailure(notification, e);
            }
        }
    }


    /**
     * ✉️ Sends an email using configured JavaMailSender.
     */
    private void sendEmail(Notification notification) {
        // ✉️ Build email object
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getUser().getEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        // 🚀 Send via Gmail (configured in application.properties / yml)
        mailSender.send(message);
    }

    /**
     * ⚠️ Handles failures:
     * - Increments retryCount
     * - Updates status to RETRYING
     * - Reschedules next attempt with progressive backoff
     * - Marks permanently FAILED if retryCount >= maxRetries
     */
    private void handleFailure(Notification notification, Exception e) {
        int retries = notification.getRetryCount() + 1;

        if (retries >= maxRetries) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setLastAttemptAt(LocalDateTime.now());
            notification.setLastError(e.getMessage());
            notification.setRetryCount(retries);
            notificationRepository.save(notification);

            log.error("🚫 Notification ID={} permanently FAILED after {} retries",
                    notification.getId(), retries, e);
            return;
        }

        // Retry logic
        notification.setRetryCount(retries);
        notification.setStatus(NotificationStatus.RETRYING);

        // 🕒 Calculate progressive backoff
        long delayMinutes = (long) backoffMinutes * notification.getRetryCount();

        notification.setScheduledTime(LocalDateTime.now().plusMinutes(delayMinutes));
        notification.setLastAttemptAt(LocalDateTime.now());
        notification.setLastError(e.getMessage());

        // Persist update
        notificationRepository.save(notification);

        log.warn("⏳ Retry scheduled for notification ID={} (attempt {} of {}) in {} minutes",
                notification.getId(), retries, maxRetries, backoffMinutes * retries, e);
    }
}

