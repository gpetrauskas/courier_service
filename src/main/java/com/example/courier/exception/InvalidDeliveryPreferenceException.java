package com.example.courier.exception;

public class InvalidDeliveryPreferenceException extends RuntimeException {
    public InvalidDeliveryPreferenceException(String message) {
        super(message);
    }
}
