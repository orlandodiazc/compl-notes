package com.ditod.notes.web.user.dto;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Hidden
public interface UserBaseDto {
    @NotNull
    UUID getId();

    @NotNull
    String getUsername();

    @NotNull
    String getEmail();

    String getName();

    UserImageSummary getImage();

    interface UserImageSummary {
        @NotNull
        UUID getId();

        @NotNull
        Instant getUpdatedAt();
    }
}
