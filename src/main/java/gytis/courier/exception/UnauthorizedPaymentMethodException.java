package gytis.courier.exception;

public class UnauthorizedPaymentMethodException extends RuntimeException {
    public UnauthorizedPaymentMethodException(String message) {
        super(message);
    }
}
