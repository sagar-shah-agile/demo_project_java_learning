package com.javatraining.notification_mgmt.service.scheduler;

import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.model.User;
import com.javatraining.notification_mgmt.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    /**
     * 🔔 Scheduled Job (CRON-based)
     *
     * This method executes automatically at fixed intervals:
     *  - "0 * * * * *" → at 0 seconds of every minute
     *
     * Workflow:
     *  1. Fetch all notifications from DB that:
     *       - are scheduled for now or earlier (scheduledTime <= current time)
     *       - have NOT been sent yet (sent = false)
     *  2. For each notification, send an email using Gmail SMTP
     *  3. Update the notification record:
     *       - set sent = true
     *       - persist changes to DB
     *
     * ✅ This ensures emails are delivered automatically once they become due.
     */
    @Scheduled(cron = "0 * * * * *")  // runs every 1 minute at 0 sec
    //@Scheduled(cron = "0 */5 * * * *") // every 5 minutes
    // OR
    @Scheduled(fixedDelay = 300000) // 5 minutes after last execution ends
    @Transactional(readOnly = false)
    public void processDueNotifications() {
        log.info("🔍 Checking for due notifications at {}", LocalDateTime.now());

        // 1️⃣ Fetch pending notifications whose time has come
        List<Notification> dueNotifications =
                notificationRepository.findByScheduledTimeBeforeAndSentFalse(LocalDateTime.now());

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
                // ✉️ Build email object
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(notification.getUser().getEmail());
                message.setSubject(notification.getSubject());
                message.setText(notification.getMessage());

                // 🚀 Send via Gmail (configured in application.properties / yml)
                mailSender.send(message);

                // 3️⃣ Update status in DB
                notification.setSent(true);
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
                // ⚠️ Error handling: log and move on to the next notification
                log.error("❌ Failed to send notification ID={} | Subject='{}' | Error: {}",
                        notification.getId(),
                        notification.getSubject(),
                        e.getMessage(), e);
            }
        }
    }

}
