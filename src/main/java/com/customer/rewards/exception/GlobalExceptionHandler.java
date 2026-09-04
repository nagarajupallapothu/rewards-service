package com.customer.rewards.exception;

import com.customer.rewards.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(
            CustomerNotFoundException exception) {

        log.warn(
                "Customer not found. message={}",
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "CUSTOMER_NOT_FOUND",
                exception.getMessage()
        );
    }


    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateRange(
            InvalidDateRangeException exception) {

        log.warn(
                "Invalid date range. message={}",
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_DATE_RANGE",
                exception.getMessage()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception) {

        String message = exception
                .getConstraintViolations()
                .stream()
                .map(violation ->
                        violation.getPropertyPath()
                                + ": "
                                + violation.getMessage())
                .collect(Collectors.joining(", "));

        log.warn(
                "Request validation failed. message={}",
                message
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        log.warn(
                "Invalid request parameter. parameter={}, value={}",
                exception.getName(),
                exception.getValue()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "Invalid value for parameter: "
                        + exception.getName()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception) {

        log.error(
                "Unexpected error while processing request",
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex) {

        log.warn(
                "Resource not found: {}",
                ex.getResourcePath()
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NoResourceFoundException",
                "The requested resource was not found"
        );

    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }

}