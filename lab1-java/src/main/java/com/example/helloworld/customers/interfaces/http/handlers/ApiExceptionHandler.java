package com.example.helloworld.customers.interfaces.http.handlers;

import com.example.helloworld.customers.domain.exceptions.CustomerNotFoundException;
import com.example.helloworld.customers.domain.exceptions.DuplicateCustomerException;
import com.example.helloworld.customers.domain.exceptions.InvalidCustomerException;
import com.example.helloworld.customers.domain.exceptions.IdempotencyKeyConflictException;
import com.example.helloworld.customers.domain.exceptions.StaleCustomerVersionException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(CustomerNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("detail", "Customer not found"));
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateCustomerException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("detail", exception.getMessage()));
    }

    @ExceptionHandler({IdempotencyKeyConflictException.class, StaleCustomerVersionException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("detail", exception.getMessage()));
    }

    @ExceptionHandler({InvalidCustomerException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidation(Exception exception) {
        String detail = exception instanceof InvalidCustomerException
                ? exception.getMessage()
                : "Request validation failed";
        return ResponseEntity.unprocessableEntity().body(Map.of("detail", detail));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch() {
        return ResponseEntity.unprocessableEntity()
                .body(Map.of("detail", "Request parameter has an invalid format"));
    }
}
