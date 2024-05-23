package com.ditod.notes.domain.note.dto;

import com.ditod.notes.domain.note_image.NoteImage;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NoteSummaryResponse(@NotNull UUID id, @NotNull String title,
                                  @NotNull String content,
                                  @NotNull OwnerSummary owner,
                                  @NotNull Instant createdAt,
                                  @NotNull Instant updatedAt,
                                  @NotNull List<NoteImage> images) {
    public record OwnerSummary(@NotNull UUID id) {
    }
}
