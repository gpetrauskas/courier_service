package gytis.courier.exception;

public class TaskNotCancelableException extends RuntimeException {
    public TaskNotCancelableException(String message) {
        super(message);
    }
}
