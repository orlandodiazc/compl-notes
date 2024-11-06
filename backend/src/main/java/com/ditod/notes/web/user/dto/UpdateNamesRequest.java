package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateNamesRequest(
        @NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username length must be " +
                "between 3 " + "and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can " +
                "only" + " " + "include " + "letters, numbers, and underscores") String username,

        @Size(min = 3, max = 40, message = "Name length must be between 3 " + "and" + " 40 characters") String name) {}
