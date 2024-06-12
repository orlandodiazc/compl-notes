package com.ditod.notes.domain.note.dto;

import com.ditod.notes.domain.note_image.NoteImage;
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

    @NotNull List<NoteImage> getImages();

    interface OwnerSummary {
        @NotNull UUID getId();
    }
}
