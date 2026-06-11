package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.dto.ApiErrorResponse;
import com.enviro.assessment.junior.paballo.exception.AgeRestrictionException;
import com.enviro.assessment.junior.paballo.exception.InsufficientBalanceException;
import com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException;
import com.enviro.assessment.junior.paballo.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .code(status.value())
                        .status(status.getReasonPhrase())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build(),
                status
        );
    }

    @ExceptionHandler({InvestorNotFoundException.class,
            ProductNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InsufficientBalanceException.class, AgeRestrictionException.class})
    public ResponseEntity<ApiErrorResponse> handleBusinessRuleException(RuntimeException ex, WebRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex.getReason(), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
