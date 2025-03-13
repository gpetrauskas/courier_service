package com.example.courier.exception;

public class DeliveryOptionNotFoundException extends RuntimeException {
    public DeliveryOptionNotFoundException(String message) {
        super(message);
    }
}
