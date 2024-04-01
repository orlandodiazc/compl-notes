package com.ditod.notes.domain.user.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public interface UserSummaryDTO extends UserBaseDTO {
    @NotNull
    Instant getCreatedAt();
}
