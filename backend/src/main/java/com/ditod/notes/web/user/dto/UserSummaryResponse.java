package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public interface UserSummaryResponse extends UserBaseDto {
    @NotNull
    Instant getCreatedAt();

    @NotNull
    Instant getUpdatedAt();
}
