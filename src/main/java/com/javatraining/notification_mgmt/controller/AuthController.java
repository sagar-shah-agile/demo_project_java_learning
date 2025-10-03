package com.javatraining.notification_mgmt.controller;

import com.javatraining.notification_mgmt.dto.request.AuthRequestDto;
import com.javatraining.notification_mgmt.dto.response.AuthResponseDto;
import com.javatraining.notification_mgmt.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * ✅ Login endpoint
     * - Takes email & password
     * - Authenticates with AuthenticationManager
     * - If valid → generates JWT and returns it
     */
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto request) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Generate JWT for authenticated user
            String token = jwtService.generateToken(authentication.getName());

            return new AuthResponseDto(token);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password", e);
        }

    }
}
