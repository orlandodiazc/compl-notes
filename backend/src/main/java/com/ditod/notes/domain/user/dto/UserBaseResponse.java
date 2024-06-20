package com.ditod.notes.domain.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public interface UserBaseResponse {
    @NotNull UUID getId();

    @NotNull String getUsername();

    String getName();

    UserImageSummary getImage();

    interface UserImageSummary {
        @NotNull UUID getId();
    }
}
