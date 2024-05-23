package com.ditod.notes.web.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only include letters, numbers, and underscores") String username,

        @NotBlank(message = "Name is required") @Size(min = 3, max = 40, message = "Name length must be between 3 and 40 characters") String name,

        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") @Size(min = 3, max = 100, message = "Email length must be between 3 and 100 characters") String email,

        @NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters") String password,

        @NotBlank(message = "Confirm password is required") @Size(min = 6, max = 100, message = "Confirm password length must be between 6 and 100 characters") String confirmPassword,

        @AssertTrue(message = "You must agree to the terms of service and privacy policy") boolean agreeToTermsOfServiceAndPrivacyPolicy) {
    @AssertTrue(message = "The passwords must match")
    public boolean isPasswordsMatch() {
        return password.equals(confirmPassword);
    }
}