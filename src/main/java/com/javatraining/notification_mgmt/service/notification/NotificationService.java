package com.javatraining.notification_mgmt.service.notification;

import com.javatraining.notification_mgmt.dto.request.NotificationRequestDto;
import com.javatraining.notification_mgmt.dto.response.NotificationResponseDto;

import java.util.List;

public interface NotificationService {
    public NotificationResponseDto createNotification(NotificationRequestDto request);
    public List<NotificationResponseDto> getAllNotifications();

}
