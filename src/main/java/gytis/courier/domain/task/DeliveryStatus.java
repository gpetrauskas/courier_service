package gytis.courier.domain.task;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the lifecycle states of a delivery.
 *
 * <p>Lifecycle:</p>
 * <ul>
 *     <li>{@code IN_PROGRESS} - Initial task status, which is set after creation</li>
 *     <li>{@code ASSIGNED} - // Not yet used </li>
 *     <li>{@code RETURNING_TO_STATION} - Courier has all items in FINAL_STATE and returning</li>
 *     <li>{@code At_CHECKPOINT} - Courier returned to station and now waits for admin set COMPLETE</li>
 *     <li>{@code COMPLETED} - Admin confirmed/validated courier return and set status to COMPLETED</li>
 *     <li>{@code CANCELED} - Delivery task was canceled</li>
 * </ul>
 */
public enum DeliveryStatus {
    IN_PROGRESS,
    COMPLETED,
    ASSIGNED,
    CANCELED,
    RETURNING_TO_STATION,
    AT_CHECKPOINT;

    /**
     * @return statuses considered "current" (active, on the road)
     */
    public static Set<DeliveryStatus> currentStatuses() {
        return EnumSet.of(IN_PROGRESS, RETURNING_TO_STATION);
    }

    /**
     * @return statuses considered "historical" (completed or canceled)
     */
    public static Set<DeliveryStatus> historicalStatuses() {
        return EnumSet.of(COMPLETED, CANCELED);
    }

    /**
     * Validates a string and returns the matching status.
     *
     * @param status the status string
     * @return the matching DeliveryStatus
     * @throws IllegalArgumentException if no match is found
     */
    public static DeliveryStatus validateAndGetStatus(String status) {
        return Arrays.stream(DeliveryStatus.values())
                .filter(e -> e.name().equals(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
    }

    /**
     * Checks if status is in a final state (cannot transition anymore).
     */
    public boolean isFinalState() {
        return historicalStatuses().contains(this);
    }

    public static Optional<DeliveryStatus> fromString(String status) {
        if (status == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst();
    }

    /**
     * Checks if this status can transition to the next given status.
     *
     * @param nextStatus the proposed next status
     * @return true if the transition is valid
     */
    public boolean canTransitionTo(DeliveryStatus nextStatus) {
        return switch (this) {
            case IN_PROGRESS -> nextStatus == RETURNING_TO_STATION || nextStatus == CANCELED;
            case RETURNING_TO_STATION -> nextStatus == AT_CHECKPOINT;
            case AT_CHECKPOINT -> nextStatus == COMPLETED;
            default -> false;
        };
    }
}
