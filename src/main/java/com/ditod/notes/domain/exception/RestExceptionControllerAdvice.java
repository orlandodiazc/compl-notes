package com.ditod.notes.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler({UsernameDoesNotExistException.class, NoteDoesNotExistException.class})
    public ProblemDetail exceptionDoesNotExistHandler(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}