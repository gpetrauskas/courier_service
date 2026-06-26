package gytis.courier.domain.notification;

public sealed interface NotificationTarget {
    record Broadcast(NotificationTargetType type) implements NotificationTarget {}
    record Individual(Long personId) implements NotificationTarget {}
}
