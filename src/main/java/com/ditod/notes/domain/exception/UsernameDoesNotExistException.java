package com.ditod.notes.domain.exception;

public class UsernameDoesNotExistException extends RuntimeException {
    public UsernameDoesNotExistException() {
        super("No user with the username exists");
    }

    public UsernameDoesNotExistException(String username) {
        super("No user with the username " + username + " exists");
    }
}
