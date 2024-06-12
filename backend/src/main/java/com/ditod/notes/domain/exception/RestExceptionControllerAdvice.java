package com.ditod.notes.domain.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class RestExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler({EntityNotFoundException.class})
    public ProblemDetail exceptionDoesNotExistHandler(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        HashMap<String, List<String>> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            errors.putIfAbsent(fieldName, new ArrayList<>());
            errors.get(fieldName).add(errorMessage);
        }
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setProperty("errors", errors);
        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }


    @ExceptionHandler({EntityAlreadyExistsException.class})
    public ProblemDetail exceptionAlreadyExistsHandler(
            EntityAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        HashMap<String, String> errors = new HashMap<>();
        errors.put(ex.getField(), ex.getMessage());
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

}