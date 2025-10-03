package com.javatraining.notification_mgmt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(example = "John Doe", description = "Full name of the user", required = true)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "john.doe@example.com", description = "Unique email of the user",required = true)
    private String email;

    @Size(max = 15, message = "Phone number must not exceed 15 digits")
    @Schema(example = "+91-9876543210", description = "User's phone number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    @Schema(description = "Password must be minimum 6 characters long")
    private String password;
}
