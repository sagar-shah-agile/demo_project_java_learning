package com.javatraining.notification_mgmt.repository;

import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 🔹 Fetch notifications that are pending or retrying, scheduled to be sent,
     * and retryCount is below the maxRetries limit.
     *
     * ✅ Uses JOIN FETCH to also load the associated user to avoid LazyInitializationException.
     */
    @Query("""
        SELECT n FROM Notification n
        JOIN FETCH n.user u
        WHERE n.scheduledTime <= :now
          AND n.status IN ('PENDING', 'RETRYING')
          AND n.retryCount < :maxRetries
    """)
    List<Notification> findPendingNotificationsWithRetry(
            @Param("now") LocalDateTime now,
            @Param("maxRetries") int maxRetries
    );

    /**
     * 🔹 Fetch permanently failed notifications (status = FAILED) with retryCount above limit.
     */
    List<Notification> findByStatusAndRetryCountGreaterThan(
            NotificationStatus status,
            int retryLimit
    );

}
