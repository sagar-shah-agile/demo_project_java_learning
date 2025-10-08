package com.javatraining.notification_mgmt.service.notification.email;

import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.service.notification.NotificationFailureSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final NotificationFailureSimulator failureSimulator;

    @Override
    public void send(Notification notification) {
        try
        {
            // 💣 Optionally simulate a failure
            failureSimulator.maybeFail(notification);

            // ✉️ Build the mail message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getUser().getEmail());
            message.setSubject(notification.getSubject());
            message.setText(notification.getMessage());

            // 🚀 Send via configured SMTP provider
            mailSender.send(message);

            // Only minimal logging here; scheduler handles main logs
            log.debug("📨 Email sent attempt for Notification ID={} to {}",
                    notification.getId(), notification.getUser().getEmail());

        } catch (MailSendException simulated) {
            // Only minimal logging here; scheduler handles main logs
            log.warn("🧪 Simulated failure for Notification ID={} | {}", notification.getId(), simulated.getMessage());
            throw simulated;  // propagate to scheduler for retry logic
        } catch (Exception realError) {
            // 🚨 Actual sending issue (e.g. SMTP error)
            log.error("❌ Real email sending failure for {}: {}",
                    notification.getUser().getEmail(), realError.getMessage(), realError);
            throw new MailSendException("Failed to send email: " + realError.getMessage(), realError);
        }
    }
}
