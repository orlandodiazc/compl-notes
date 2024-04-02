package com.ditod.notes.domain.note.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NoteUsernameAndIdResponse(@NotNull UUID id,
                                        @NotNull String username) {
}
