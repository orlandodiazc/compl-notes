package com.ditod.notes.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class RestExceptionControllerAdvice {
    @ExceptionHandler({EntityDoesNotExistException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleDoesNotExistException(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        HashMap<String, List<String>> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            errors.putIfAbsent(fieldName, new ArrayList<>());
            errors.get(fieldName).add(errorMessage);
        }
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler({EntityAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleAlreadyExistsException(EntityAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAuthenticationException() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                                                "Invalid username or password. Please try again.");
    }
}