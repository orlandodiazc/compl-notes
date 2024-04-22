package com.ditod.notes.domain.note.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NoteSummaryResponse {
    @NotNull UUID getId();

    @NotNull String getTitle();

    @NotNull String getContent();

    @NotNull OwnerSummary getOwner();

    @NotNull Instant getUpdatedAt();

    @NotNull List<NoteImageSummary> getImages();

    interface OwnerSummary {
        @NotNull UUID getId();
    }

    interface NoteImageSummary {
        @NotNull UUID getId();

        String getAltText();
    }
}
