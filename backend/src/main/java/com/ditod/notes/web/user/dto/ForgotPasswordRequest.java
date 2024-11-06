package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @NotBlank(message = "Username or email is required") @Size(min = 3, max = 100, message =
                "Usename or email " + "length must be between 3 and 100 characters") String usernameOrEmail) {}
