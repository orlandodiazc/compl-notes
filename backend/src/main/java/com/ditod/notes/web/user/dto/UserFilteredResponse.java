package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserFilteredResponse {
    @NotNull
    UUID getId();

    @NotNull
    String getUsername();

    String getName();

    UUID getImageId();
}

