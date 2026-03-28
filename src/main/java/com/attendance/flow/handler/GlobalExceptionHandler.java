package com.attendance.flow.handler;

import com.attendance.flow.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Data validation error");
        problemDetail.setTitle("Validation error");

        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        problemDetail.setProperties(errors);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Data read error");
        problemDetail.setTitle("Malformed JSON Request");
        problemDetail.setProperty("specific_cause", ex.getMostSpecificCause().getMessage());
        return problemDetail;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(AlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Data already exists");
        problemDetail.setTitle("Data conflict");
        problemDetail.setProperty("error", ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        problemDetail.setTitle("Internal server error");
        problemDetail.setProperty("error", ex.getMessage());
        return problemDetail;
    }
}