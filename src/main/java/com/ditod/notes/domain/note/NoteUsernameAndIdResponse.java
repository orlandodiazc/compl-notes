package com.ditod.notes.domain.note;

import java.util.UUID;

public record NoteUsernameAndIdResponse(UUID id, String username) {
}
