package com.example.courier.exception;

public class TaskNotCancelableException extends RuntimeException {
    public TaskNotCancelableException(String message) {
        super(message);
    }
}
