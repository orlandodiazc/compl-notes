package com.ditod.notes.domain.exception;

public class UserDoesNotExistException extends EntityDoesNotExistException {
    public UserDoesNotExistException() {
        super();
    }

    public UserDoesNotExistException(String username) {
        super("user", "username", username);
    }
}
