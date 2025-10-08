package com.javatraining.notification_mgmt.service.notification;

import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationFailureSimulator {

    private final NotificationRepository notificationRepository;

    @Value("${notification.simulate-failure}")
    private boolean simulateFailures;

    public void maybeFail(Notification notification) {
        if (!simulateFailures) return;

        // 🎯 Only deterministic failure: Subject must start with "TESTING FAIL"
        boolean shouldFail = notification.getSubject().toUpperCase().startsWith("FAIL");

        if (shouldFail) {
            log.warn("🧪 Simulating deterministic failure for Notification ID={} | Subject='{}'",
                    notification.getId(), notification.getSubject());
            throw new MailSendException("🧪 Simulated email failure for testing");
        }
    }

    @PostConstruct
    void init() {
        if (simulateFailures) {
            log.info("🧪 Notification failure simulation mode is ACTIVE");
        }
    }

}
