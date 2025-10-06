package com.javatraining.notification_mgmt.controller;

import com.javatraining.notification_mgmt.dto.request.NotificationRequestDto;
import com.javatraining.notification_mgmt.dto.response.NotificationResponseDto;
import com.javatraining.notification_mgmt.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Manage notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(@Valid @RequestBody NotificationRequestDto request)
    {
        NotificationResponseDto response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications()
    {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}
