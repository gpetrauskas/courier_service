package com.example.courier.exception;

public class PaymentAlreadyMadeException extends RuntimeException {
    public PaymentAlreadyMadeException(String message) {
        super(message);
    }
}
