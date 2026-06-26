package gytis.courier.domain.task;

import java.util.Arrays;
import java.util.Optional;

/**
 * Type of the task.
 *
 * <ul>
 *     <li>{@code PICKUP} - task type for picking up parcels from the senders</li>
 *     <li>{@code DELIVERY} - task type for delivering parcels to the recipients</li>
 * </ul>*/
public enum TaskType {
    DELIVERY,
    PICKUP;

    /**
     * Returns valid TaskType from a string
     *
     * @param type task type as a string
     * @return a valid task type
     * @throws IllegalArgumentException if status is unknown
     */
    public static Optional<TaskType> fromString(String type) {
        if (type == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst();
    }
}
