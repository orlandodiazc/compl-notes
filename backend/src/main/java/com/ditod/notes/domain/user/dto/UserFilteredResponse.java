package com.ditod.notes.domain.user.dto;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserFilteredResponse {
    @NotNull UUID getId();

    @NotNull String getUsername();

    String getName();

    UUID getImageId();
}

