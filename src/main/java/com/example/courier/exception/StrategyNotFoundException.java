package com.example.courier.exception;

public class StrategyNotFoundException extends RuntimeException {
    public StrategyNotFoundException(String message) {
        super(message);
    }
}
