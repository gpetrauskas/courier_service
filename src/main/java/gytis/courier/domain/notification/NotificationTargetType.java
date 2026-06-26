package gytis.courier.domain.notification;

/**
 * Represents the target type of recipients for notification.
 *
 * <p>Notifications can be targeted to:</p>
 * <ul>
 *     <li>{@code USER} - users in the system</li>
 *     <li>{@code COURIER} - all couriers</li>
 *     <li>{@code ADMIN} - all admins</li>
 * </ul>*/
public enum NotificationTargetType {
    USER, COURIER, ADMIN
}
