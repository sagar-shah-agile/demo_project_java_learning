package com.javatraining.notification_mgmt.repository;

import com.javatraining.notification_mgmt.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
    /**
     * Finds all notifications that:
     *  - should have been sent already (scheduledTime <= now)
     *  - are not yet marked as sent
     */
    List<Notification> findByScheduledTimeBeforeAndSentFalse(LocalDateTime now);

    /**
     * (Optional extension)
     * Fetch notifications that failed too many times
     */
    //List<Notification> findBySentFalseAndRetryCountGreaterThan(int retryLimit);
}
