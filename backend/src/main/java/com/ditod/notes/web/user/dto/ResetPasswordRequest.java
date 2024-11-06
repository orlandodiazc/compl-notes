package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password length must be " +
                "between 6 and 100 characters") String password,
        @NotBlank(message = "Confirm password is required") @Size(min = 6, max = 100, message =
                "Confirm password " + "length must be between 6 and 100 characters") String confirmPassword) {
    @AssertTrue(message = "The passwords must match")
    private boolean isPasswordMatch() {
        return password.equals(confirmPassword);
    }
}
