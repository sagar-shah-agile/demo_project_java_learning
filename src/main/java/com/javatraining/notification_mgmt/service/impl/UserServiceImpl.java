package com.javatraining.notification_mgmt.service.impl;


import com.javatraining.notification_mgmt.dto.request.UserRequestDto;
import com.javatraining.notification_mgmt.dto.response.NotificationResponseDto;
import com.javatraining.notification_mgmt.dto.response.UserResponseDto;
import com.javatraining.notification_mgmt.exception.custom.DuplicateEmailException;
import com.javatraining.notification_mgmt.exception.custom.UserNotFoundException;
import com.javatraining.notification_mgmt.model.Notification;
import com.javatraining.notification_mgmt.model.User;
import com.javatraining.notification_mgmt.repository.UserRepository;
import com.javatraining.notification_mgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /*private final UserMapper userMapper;*/

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        // Check for duplicate email
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new DuplicateEmailException("Email already exists: " + request.getEmail());
                });

        // Map DTO → Entity using ModelMapper
        User user = modelMapper.map(request, User.class);

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        // Map Entity → DTO
        return modelMapper.map(saved, UserResponseDto.class);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email-id: " + email));

        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        // Avoid duplicate email conflict if email is changed
        // Check only if email is updated
        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existing -> {
                        throw new DuplicateEmailException("Email already exists: " + request.getEmail());
                    });
        }

        // Map DTO → existing entity (only updates matching fields)
        modelMapper.map(request, user);

        // Encode password if changed
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);

        return modelMapper.map(updated, UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUserByEmail(String email, UserRequestDto request) {
        // Fetch user by email from JWT (logged-in user)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Optional: If you allow changing email, check duplicate
        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existing -> {
                        throw new DuplicateEmailException("Email already exists: " + request.getEmail());
                    });
            user.setEmail(request.getEmail());
        }

        // Update other fields if provided
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);

        // Map to DTO and return
        return modelMapper.map(updated, UserResponseDto.class);
    }


    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        userRepository.delete(user);
    }

    @Override
    public List<NotificationResponseDto> getUserNotifications(Long userId) {

        // 1️⃣ Fetch the user first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // 2️⃣ Get notifications from the user entity
        List<Notification> notifications = user.getNotifications();

        // 3️⃣ Map Entity → DTO using ModelMapper
        return notifications.stream()
                .map(notification -> {
                    NotificationResponseDto dto = modelMapper.map(notification, NotificationResponseDto.class);
                    dto.setRecipientEmail(notification.getUser().getEmail()); // ✅ set email manually
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

