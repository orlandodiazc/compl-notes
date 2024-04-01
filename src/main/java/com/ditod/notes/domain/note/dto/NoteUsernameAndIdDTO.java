package com.ditod.notes.domain.note.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NoteUsernameAndIdDTO(@NotNull UUID id, @NotNull String username) {
}
