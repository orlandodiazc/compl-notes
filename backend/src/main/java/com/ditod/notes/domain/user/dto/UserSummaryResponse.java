package com.ditod.notes.domain.user.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public interface UserSummaryResponse extends UserBaseResponse {
    @NotNull Instant getCreatedAt();

    @NotNull Instant getUpdatedAt();
}
