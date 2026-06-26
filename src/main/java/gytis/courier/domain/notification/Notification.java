package gytis.courier.domain.notification;

import java.util.Objects;

public class Notification {
    private Long id;
    private final String title;
    private final String message;
    private final NotificationTarget target;

    public Notification(String title, String message, NotificationTarget target) {
        validateTitle(title);
        validateMessage(message);

        this.title = title;
        this.message = message;
        this.target = target;
    }

    public Notification withId(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationTarget getTarget() { return target; }

    private void validateTitle(String title) {
        Objects.requireNonNull(title);
        if (title.length() > 30) throw new IllegalArgumentException("Title cannot be longer than 20 characters");
        if (!title.matches("^[a-zA-Z].*")) throw new IllegalArgumentException("Title must start with a letter");
    }

    private void validateMessage(String message) {
        Objects.requireNonNull(message);
        if (message.isEmpty() || message.length() > 200) throw new IllegalArgumentException("Message cannot be empty or longer than 200 characters");
    }
}
