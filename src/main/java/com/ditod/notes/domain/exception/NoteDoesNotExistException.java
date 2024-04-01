package com.ditod.notes.domain.exception;

import java.util.UUID;

public class NoteDoesNotExistException extends RuntimeException {
    public NoteDoesNotExistException() {
        super("Note was not found");
    }

    public NoteDoesNotExistException(UUID id) {
        super("No note with the id " + id + " exists");
    }
}
