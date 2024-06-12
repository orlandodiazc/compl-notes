package com.ditod.notes.domain.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException() {
        super("Not Found");
    }

    public EntityNotFoundException(String entity, String description) {
        super("No " + entity + " with the name " + description + " exists");
    }

    public EntityNotFoundException(String entity, UUID id) {
        super("No " + entity + " with the id " + id + " exists");
    }
}
