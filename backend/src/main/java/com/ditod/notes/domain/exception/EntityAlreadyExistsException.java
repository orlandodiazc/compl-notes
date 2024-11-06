package com.ditod.notes.domain.exception;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String entity, String field) {
        super("A " + entity + " already exists with this " + field);
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
