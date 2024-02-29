package com.example.courier.exception;

public class PricingOptionNotFoundException extends RuntimeException {
    public PricingOptionNotFoundException(String message) {
        super(message);
    }
}
