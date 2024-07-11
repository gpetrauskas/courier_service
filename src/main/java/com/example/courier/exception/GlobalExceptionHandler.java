package com.example.courier.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userNotFoundException.getMessage());
    }

    @ExceptionHandler(UnauthorizedPaymentMethodException.class)
    public ResponseEntity<String> handleUnauthorizedPaymentMethodException(UnauthorizedPaymentMethodException un) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(un.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFoundException(OrderNotFoundException on) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(on.getMessage());
    }

    @ExceptionHandler(PaymentAlreadyMadeException.class)
    public ResponseEntity<String> handlePaymentAlreadyMadeException(PaymentAlreadyMadeException pa) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pa.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
            logger.warn("Validating error on field {}: {}", error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "An unexpected error occurred");
        logger.error("Runtime exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
