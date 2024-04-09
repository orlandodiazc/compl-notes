package com.ditod.notes.domain.note.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NoteSummaryResponse {
    @NotNull UUID getId();

    String getTitle();

    String getContent();

    @NotNull OwnerSummary getOwner();

    @NotNull Instant getUpdatedAt();

    @NotNull List<ImageSummary> getImages();

    interface OwnerSummary {
        @NotNull UUID getId();
    }

    interface ImageSummary {
        @NotNull UUID getId();

        String getAltText();
    }
}
