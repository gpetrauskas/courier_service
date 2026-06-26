package gytis.courier.exception;

public class OrderCancellationException extends  RuntimeException {
    public OrderCancellationException(String message) {
        super(message);
    }
}
