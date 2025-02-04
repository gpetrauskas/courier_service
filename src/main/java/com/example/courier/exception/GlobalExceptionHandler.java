package com.example.courier.exception;

import com.example.courier.dto.ApiResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
            logger.warn("Validation error on field {}: {}", error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid argument: {}", ex.getMessage());
        ApiResponseDTO errorResponse = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({UserNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiResponseDTO> handleNotFoundException(RuntimeException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
    }

    @ExceptionHandler({UnauthorizedPaymentMethodException.class, UnauthorizedAccessException.class})
    public ResponseEntity<ApiResponseDTO> handleUnauthorizedExceptions(RuntimeException ex) {
        logger.error("Unauthorized access: {}", ex.getMessage());
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseDTO);
    }

    @ExceptionHandler(PaymentAlreadyMadeException.class)
    public ResponseEntity<ApiResponseDTO> handlePaymentAlreadyMadeException(PaymentAlreadyMadeException ex) {
        logger.error("Payment error: {}", ex.getMessage());
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
    }

    @ExceptionHandler(OrderCancellationException.class)
    public ResponseEntity<ApiResponseDTO> handleOrderCancellationException(OrderCancellationException ex) {
        logger.error("Order cancellation error: {}", ex.getMessage());
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", "Unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
    }
}