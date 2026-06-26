package gytis.courier.exception;

public class NoRecipientFoundException extends RuntimeException {
    public NoRecipientFoundException(String personType) {
        super("No recipients found for: " + personType);
    }
}
