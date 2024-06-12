package com.ditod.notes.domain.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserBaseResponse {
    @NotNull UUID getId();

    @NotNull String getUsername();

    String getName();

    UserImageSummary getImage();

    interface UserImageSummary {
        @NotNull UUID getId();
    }
}
