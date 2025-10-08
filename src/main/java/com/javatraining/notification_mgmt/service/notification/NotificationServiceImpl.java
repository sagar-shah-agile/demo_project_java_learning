package com.javatraining.notification_mgmt.service.notification;

import com.javatraining.notification_mgmt.dto.request.NotificationRequestDto;
import com.javatraining.notification_mgmt.dto.response.NotificationResponseDto;
import com.javatraining.notification_mgmt.exception.custom.UserNotFoundException;
import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.model.User;
import com.javatraining.notification_mgmt.model.enums.NotificationStatus;
import com.javatraining.notification_mgmt.repository.NotificationRepository;
import com.javatraining.notification_mgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public NotificationResponseDto createNotification(NotificationRequestDto request) {
        // ✅ 1️⃣ Validate & fetch recipient user
        User user = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> new UserNotFoundException("No user found with email: " + request.getRecipientEmail()));

        // ✅ 2️⃣ Map Request → Entity
        Notification notification = modelMapper.map(request, Notification.class);
        notification.setUser(user);
        // ⚙️ Initialize system-managed fields
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);
        notification.setLastAttemptAt(null);
        notification.setLastError(null);

        // ✅ 3️⃣ Save to DB
        Notification saved = notificationRepository.save(notification);

        // ✅ 4️⃣ Map Entity → Response DTO
        NotificationResponseDto response = modelMapper.map(saved, NotificationResponseDto.class);
        response.setRecipientEmail(user.getEmail());

        log.info("🆕 Notification created for user={} | subject='{}' | scheduledTime={}",
                user.getEmail(), request.getSubject(), request.getScheduledTime());

        return response;
    }

    /**
     * 📜 Fetch all notifications (with recipient email)
     */
    @Override
    public List<NotificationResponseDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notification -> {
                    NotificationResponseDto dto = modelMapper.map(notification, NotificationResponseDto.class);

                    // 💡 Manually set recipientEmail from related User
                    if (notification.getUser() != null) {
                        dto.setRecipientEmail(notification.getUser().getEmail());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
