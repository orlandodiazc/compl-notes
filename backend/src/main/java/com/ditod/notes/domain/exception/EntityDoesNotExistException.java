package com.ditod.notes.domain.exception;

import java.util.UUID;

public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException() {
        super("Not Found");
    }

    public EntityDoesNotExistException(String entity, String field, String value) {
        super("No " + entity + " exists with the " + field + ": " + value);
    }

    public EntityDoesNotExistException(String entity, UUID id) {
        super("No " + entity + " exists with the id: " + id);
    }
}
