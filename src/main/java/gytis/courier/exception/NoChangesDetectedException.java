package gytis.courier.exception;

public class NoChangesDetectedException extends RuntimeException {
    private final String target;
    private final Long id;

    public NoChangesDetectedException(String target, Long id) {
        this.target = target;
        this.id = id;
    }

    public String getTarget() { return target; }
    public Long getId() { return id; }
}
