package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeEmailRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") @Size(min = 3, max = 100,
                message = "Email length must be between 3 and 100 characters") String email) {}
