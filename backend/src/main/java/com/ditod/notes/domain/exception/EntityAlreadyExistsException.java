package com.ditod.notes.domain.exception;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String entity, String field, String value) {
        super("A " + entity + " already exists with the " + field + ": " + value);
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
