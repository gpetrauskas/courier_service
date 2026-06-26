package gytis.courier.exception;

public class PaymentHandlerNotFoundException extends RuntimeException {
    public PaymentHandlerNotFoundException(String message) {
        super(message);
    }
}
