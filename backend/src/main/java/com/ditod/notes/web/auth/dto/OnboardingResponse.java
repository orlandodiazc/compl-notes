package com.ditod.notes.web.auth.dto;

import jakarta.validation.constraints.NotNull;

public record OnboardingResponse(@NotNull String email) {}
