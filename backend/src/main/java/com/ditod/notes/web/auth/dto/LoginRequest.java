package com.ditod.notes.web.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username length must be " +
                "between 3 and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only " +
                "include letters, numbers, and underscores") String username,

        @NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password length must be " +
                "between 6 and 100 characters") String password,

        Boolean remember) {}