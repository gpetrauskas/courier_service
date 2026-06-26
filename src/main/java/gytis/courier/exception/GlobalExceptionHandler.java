package gytis.courier.exception;

import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.adapter.in.rest.common.ApiResponseType;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.*;

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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Bad Credentials: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, "error");
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ApiResponse> handleInvalidStateException(InvalidStateTransitionException ex) {
        logger.warn("IllegalStateException error: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT, "error");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage(), ex);
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, "error");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Error: {}", ex.getMessage());
        return errorResponse("Invalid format error", HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid argument: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler({UserNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiResponse> handleNotFoundException(RuntimeException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "error");
    }

    @ExceptionHandler({UnauthorizedPaymentMethodException.class, UnauthorizedAccessException.class})
    public ResponseEntity<ApiResponse> handleUnauthorizedExceptions(RuntimeException ex) {
        logger.error("Unauthorized access: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, "error");
    }

    @ExceptionHandler(PaymentAlreadyMadeException.class)
    public ResponseEntity<ApiResponse> handlePaymentAlreadyMadeException(PaymentAlreadyMadeException ex) {
        logger.error("Payment error: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(OrderCancellationException.class)
    public ResponseEntity<ApiResponse> handleOrderCancellationException(OrderCancellationException ex) {
        logger.error("Order cancellation error: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return errorResponse("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, "error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return errorResponse("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, "error");
    }

    @ExceptionHandler(DeliveryOptionNotFoundException.class)
    public ResponseEntity<ApiResponse> handleDeliveryOptionNotFoundException(DeliveryOptionNotFoundException ex) {
        logger.error("Delivery Option error: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "error");
    }

    @ExceptionHandler(TaskNotCancelableException.class)
    public ResponseEntity<ApiResponse> handleTaskNotCancelableException(TaskNotCancelableException ex) {
        logger.error("Task cancellation error: {}", ex.getMessage());
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse> handleNumberFormatException(NumberFormatException ex) {
        logger.error("Number format error");
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(NoRecipientFoundException.class)
    public ResponseEntity<String> handleNoRecipientFound(NoRecipientFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        String errorMessage = ex.getAllErrors().stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        logger.error("Validation error: {}", errorMessage);
        return errorResponse(errorMessage, HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(CompositeValidationException.class)
    public ResponseEntity<List<ApiResponse>> handleValidationErrors(CompositeValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiResponse> handleAddressNotFound(AddressNotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, "address_error");
    }

    @ExceptionHandler(UserAddressMismatchException.class)
    public ResponseEntity<ApiResponse> handleUserAddressMismatch(UserAddressMismatchException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, "user_address_mismatch");
    }

    @ExceptionHandler(StrategyNotFoundException.class)
    public ResponseEntity<ApiResponse> handleStrategyNotFound(StrategyNotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "strategy_error");
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ApiResponse> handlePaymentFailed(PaymentFailedException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(PaymentHandlerNotFoundException.class)
    public ResponseEntity<ApiResponse> handlePaymentHandlerNotFound(PaymentHandlerNotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(PaymentCreationException.class)
    public ResponseEntity<ApiResponse> handlePaymentCreationException(PaymentCreationException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "error");
    }

    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<ApiResponse> handleInvalidAddressException(InvalidAddressException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "error");
    }

    @ExceptionHandler(NoChangesDetectedException.class)
    public ResponseEntity<ApiResponse> handleNoChangesDetected(NoChangesDetectedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseType.NO_CHANGES_DETECTED.withParams(ex.getTarget(), ex.getId()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbiddenException(ForbiddenException ex) {
        return errorResponse(ex.getMessage(),  HttpStatus.FORBIDDEN, "error");
    }

    @ExceptionHandler(TicketClosedException.class)
    public ResponseEntity<ApiResponse> handleTicketClosedException(TicketClosedException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT, "error");
    }

    private ResponseEntity<ApiResponse> errorResponse(String message, HttpStatus status, String messageStatus) {
        return ResponseEntity.status(status).body(new ApiResponse(messageStatus, message));
    }
}