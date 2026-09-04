package com.customer.rewards.exception;

import com.customer.rewards.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();

        MDC.put(
                "correlationId",
                "test-correlation-id"
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        MDC.clear();
        mocks.close();
    }

    @Test
    void shouldHandleCustomerNotFoundException() {

        // Arrange
        CustomerNotFoundException exception =
                new CustomerNotFoundException(
                        "123"
                );

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleCustomerNotFound(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                404,
                body.getStatus()
        );

        assertEquals(
                "CUSTOMER_NOT_FOUND",
                body.getError()
        );

        assertEquals(
                "Customer not found: 123",
                body.getMessage()
        );


        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleInvalidDateRangeException() {

        // Arrange
        InvalidDateRangeException exception =
                new InvalidDateRangeException(
                        "fromDate must be before or equal to toDate"
                );

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleInvalidDateRange(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                400,
                body.getStatus()
        );

        assertEquals(
                "INVALID_DATE_RANGE",
                body.getError()
        );

        assertEquals(
                "fromDate must be before or equal to toDate",
                body.getMessage()
        );


        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleConstraintViolationException() {

        // Arrange
        ConstraintViolation<?> violation =
                mock(ConstraintViolation.class);

        Path propertyPath =
                mock(Path.class);

        when(propertyPath.toString())
                .thenReturn("customerId");

        when(violation.getPropertyPath())
                .thenReturn(propertyPath);

        when(violation.getMessage())
                .thenReturn("must not be blank");

        ConstraintViolationException exception =
                new ConstraintViolationException(
                        Set.of(violation)
                );

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleConstraintViolation(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                400,
                body.getStatus()
        );

        assertEquals(
                "VALIDATION_ERROR",
                body.getError()
        );

        assertEquals(
                "customerId: must not be blank",
                body.getMessage()
        );


        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleMultipleConstraintViolations() {

        // Arrange
        ConstraintViolation<?> customerIdViolation =
                mock(ConstraintViolation.class);

        Path customerIdPath =
                mock(Path.class);

        when(customerIdPath.toString())
                .thenReturn("customerId");

        when(customerIdViolation.getPropertyPath())
                .thenReturn(customerIdPath);

        when(customerIdViolation.getMessage())
                .thenReturn("must not be blank");


        ConstraintViolation<?> dateViolation =
                mock(ConstraintViolation.class);

        Path datePath =
                mock(Path.class);

        when(datePath.toString())
                .thenReturn("fromDate");

        when(dateViolation.getPropertyPath())
                .thenReturn(datePath);

        when(dateViolation.getMessage())
                .thenReturn("must be a valid date");


        ConstraintViolationException exception =
                new ConstraintViolationException(
                        Set.of(
                                customerIdViolation,
                                dateViolation
                        )
                );

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleConstraintViolation(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        String message =
                response.getBody().getMessage();

        assertTrue(
                message.contains(
                        "customerId: must not be blank"
                )
        );

        assertTrue(
                message.contains(
                        "fromDate: must be a valid date"
                )
        );
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {

        // Arrange
        MethodArgumentTypeMismatchException exception =
                mock(
                        MethodArgumentTypeMismatchException.class
                );

        when(exception.getName())
                .thenReturn("fromDate");

        when(exception.getValue())
                .thenReturn("invalid-date");

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleTypeMismatch(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                400,
                body.getStatus()
        );

        assertEquals(
                "INVALID_PARAMETER",
                body.getError()
        );

        assertEquals(
                "Invalid value for parameter: fromDate",
                body.getMessage()
        );


        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleGenericException() {

        // Arrange
        RuntimeException exception =
                new RuntimeException(
                        "Database connection failed"
                );

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleGenericException(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                500,
                body.getStatus()
        );

        assertEquals(
                "INTERNAL_SERVER_ERROR",
                body.getError()
        );

        assertEquals(
                "An unexpected error occurred",
                body.getMessage()
        );


        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleNoResourceFoundException() {

        // Arrange
        NoResourceFoundException exception =
                new NoResourceFoundException(
                       HttpMethod.GET,
                        "/favicon.ico"
                );

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleNoResourceFound(
                        exception
                );

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                404,
                body.getStatus()
        );

        assertEquals(
                "The requested resource was not found",
                body.getMessage()
        );
    }
}