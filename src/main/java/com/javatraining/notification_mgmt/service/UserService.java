package com.javatraining.notification_mgmt.service;

import com.javatraining.notification_mgmt.dto.request.UserRequestDto;
import com.javatraining.notification_mgmt.dto.response.UserResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    public UserResponseDto createUser(UserRequestDto request);
    public List<UserResponseDto> getAllUsers();
    public UserResponseDto getUserById(Long id);
    public UserResponseDto getUserByEmail(String email);

    public void deleteUser(Long id);
    public void deleteUserByEmail(String username);

    public UserResponseDto updateUserByEmail(String username, @Valid UserRequestDto request);
    public UserResponseDto updateUser(Long id, UserRequestDto request);
}
