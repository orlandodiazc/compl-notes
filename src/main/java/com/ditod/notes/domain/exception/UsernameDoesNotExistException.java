package com.ditod.notes.domain.exception;

import java.util.UUID;

public class UsernameDoesNotExistException extends RuntimeException {
    public UsernameDoesNotExistException() {
        super("User was not found");
    }

    public UsernameDoesNotExistException(String username) {
        super("No user with the username " + username + " exists");
    }

    public UsernameDoesNotExistException(UUID id) {
        super("No user with the id " + id + " exists");
    }
}
