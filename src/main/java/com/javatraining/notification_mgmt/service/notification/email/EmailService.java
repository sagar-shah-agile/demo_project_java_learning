package com.javatraining.notification_mgmt.service.notification.email;

import com.javatraining.notification_mgmt.model.Notification;

public interface EmailService {
    public void send(Notification notification);
}
