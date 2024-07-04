package com.example.courier.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

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
}
