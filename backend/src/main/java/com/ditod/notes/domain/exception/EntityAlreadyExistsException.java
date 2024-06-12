package com.ditod.notes.domain.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    private final String field;

    public EntityAlreadyExistsException(String entity, String field) {
        super("A " + entity + " already exists with this " + field);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
