package com.example.courier.exception;

import com.example.courier.dto.ApiResponseDTO;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseDTO> handleValidationException(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage(), ex);
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseDTO);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", "Invalid format error");
        logger.error("Error: {}", apiResponseDTO.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
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

    @ExceptionHandler(DeliveryOptionNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleDeliveryOptionNotFoundException(DeliveryOptionNotFoundException ex) {
        logger.error("Delivery Option error: {}", ex.getMessage(), ex);
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
    }

    @ExceptionHandler(TaskNotCancelableException.class)
    public ResponseEntity<ApiResponseDTO> handleTaskNotCancelableException(TaskNotCancelableException ex) {
        logger.error("Task cancellation error: {}", ex.getMessage());
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponseDTO> handleNumberFormatException(NumberFormatException ex) {
        logger.error("Number format error");
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
    }

    @ExceptionHandler(NoRecipientFoundException.class)
    public ResponseEntity<String> handleNoRecipientFound(NoRecipientFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponseDTO> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        String errorMessage = ex.getAllErrors().stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        logger.error("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO("error", errorMessage));
    }
}