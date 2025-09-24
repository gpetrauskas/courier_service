package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Preserves a lifecycle states of parcel.
 *
 * <p>Lifecycle:</p>
 * <ul>
 *     <li>{@code WAITING_FOR_PAYMENT} - Initial parcel status after order creation</li>
 *     <li>{@code PICKING_UP} - status after payment was done</li>
 *     <li>{@code PICKED_UP} - after parcel was successfully picked up from the sender</li>
 *     <li>{@code DELIVERING} - after delivery task creation (PICKED_UP status changed to DELIVERING in the task list)</li>
 *     <li>{@code DELIVERED} - after successful parcel delivery to the recipient</li>
 *     <li>{@code FAILED_PICKUP} - after courier was not able to take a parcel from the sender</li>
 *     <li>{@code FAILED_DELIVERY} - if a parcel wasnt given to a recipient for any reason</li>
 *     <li>{@code REMOVED_FROM_THE_LIST} - if item was removed from a tasklist</li>
 *     <li>{@code CANCELED} - is set after whole task was set to cancel</li>
 *     <li>{@code RETURNED_TO_CHECKPOINT} - //not yet implemented</li>
 *     <li>{@code NOT_SHIPPED} //not yet implemented</li>
 *
 *
 * </ul>
 */
public enum ParcelStatus {
    WAITING_FOR_PAYMENT,
    PICKING_UP,
    DELIVERING,
    PICKED_UP,
    DELIVERED,
    AT_CHECKPOINT,
    FAILED_PICKUP,
    FAILED_DELIVERY,
    CANCELED,
    REMOVED_FROM_THE_LIST,
    RETURNED_TO_CHECKPOINT,
    NOT_SHIPPED;

    private static final Set<String> STATUS_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private static final Set<ParcelStatus> FINAL_STATES = Set.of(PICKED_UP, DELIVERED, CANCELED, FAILED_PICKUP, FAILED_DELIVERY, REMOVED_FROM_THE_LIST);

    private static final Set<ParcelStatus> PREVENT_TASK_FROM_CANCEL = Set.of(PICKED_UP, DELIVERED);

    private static final Set<ParcelStatus> ALREADY_REMOVED_OR_CANCELED = Set.of(REMOVED_FROM_THE_LIST, CANCELED);

    /**
     * Checks if given status is considered final (cannot be modified)
     *
     * @param status status of the parcel
     * @return true if its in final state and cannot be modified and false otherwise
     */
    public static boolean cannotBeModified(ParcelStatus status) {
        return FINAL_STATES.contains(status);
    }

    /**
     * Validate that the given string corresponds to a valid ParcelStatus.
     *
     * @param status status as string
     * @throws IllegalArgumentException if status is not valid
     * */
    public static void validateStatus(String status) {
        if (status != null && !STATUS_NAMES.contains(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    /**
     * Checks if this status is in final state.
     *
     * @return true if its in final state and false otherwise
     */
    public boolean isFinalState() {
        return FINAL_STATES.contains(this);
    }

    /**
     * Checks if this status is already canceled or removed.
     *
     * @return true if already removed or canceled or false if otherwise
     */
    public boolean isAlreadyCanceledOrRemoved() {
        return ALREADY_REMOVED_OR_CANCELED.contains(this);
    }

    /**
     * Determines if this status prevents canceling the associated task.
     *
     * @return true if prevents from cancel and false if not
     */
    public boolean preventsTaskCancel() {
        return PREVENT_TASK_FROM_CANCEL.contains(this);
    }

    /**
     * Checks if transition from this status to a given new status is valid.
     *
     * @param newStatus new status
     * @return true if next transition is valid for this status to a given and false if otherwise
     * @throws IllegalArgumentException if no valid transitions found
     */
    public boolean isValidTransition(ParcelStatus newStatus) {
        return switch (this) {
            case PICKING_UP -> newStatus == PICKED_UP || newStatus == FAILED_PICKUP;
            case DELIVERING -> newStatus == DELIVERED || newStatus == FAILED_DELIVERY;
            case DELIVERED, PICKED_UP, FAILED_DELIVERY, FAILED_PICKUP -> false;
            default -> throw new IllegalArgumentException("invalid transition from: " + this + " to " + newStatus);
        };
    }

}
