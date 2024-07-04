package com.example.courier.exception;

public class UserAddressMismatchException extends RuntimeException {
    public UserAddressMismatchException(String message) {
        super(message);
    }
}
