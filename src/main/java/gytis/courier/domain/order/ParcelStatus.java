package gytis.courier.domain.order;

import gytis.courier.domain.task.TaskType;

import java.util.Set;

/**
 * Preserves a lifecycle states of parcel.
 *
 * <p>Lifecycle:</p>
 * <ul>
 *     <li>{@code WAITING_FOR_PAYMENT} - Initial parcel status after order creation</li>
 *
 *     <li>{@code PICKING_UP} - status after payment was done</li>
 *     <li>{@code PICKED_UP} - after parcel was successfully picked up from the sender</li>
 *     <li>{@code DELIVERING} - after delivery task creation (PICKED_UP status changed to DELIVERING in the task list)</li>
 *     <li>{@code DELIVERED} - after successful parcel delivery to the recipient</li>
 *
 *     <li>{@code FAILED_PICKUP} - after courier was not able to take a parcel from the sender</li>
 *     <li>{@code FAILED_DELIVERY} - if a parcel wasnt given to a recipient for any reason</li>
 *     <li>{@code REMOVED_FROM_THE_LIST} - if item was removed from a tasklist</li>
 *     <li>{@code CANCELED} - is set after whole task was set to cancel</li>
 *
 * </ul>
 */
public enum ParcelStatus {
    WAITING_FOR_PAYMENT,

    //status for both parcel and task item statuses
    PICKING_UP,
    DELIVERING,
    PICKED_UP,
    DELIVERED,

    // terminal states
    FAILED_PICKUP,
    FAILED_DELIVERY,
    CANCELED,
    REMOVED_FROM_THE_LIST;

    private static final Set<ParcelStatus> FINAL_STATES = Set.of(PICKED_UP, DELIVERED, CANCELED, FAILED_PICKUP, FAILED_DELIVERY, REMOVED_FROM_THE_LIST);

    private static final Set<ParcelStatus> CAN_BE_ADDED_TO_LIST = Set.of(PICKING_UP, DELIVERING);

    private static final Set<ParcelStatus> PREVENT_TASK_FROM_CANCEL = Set.of(PICKED_UP, DELIVERED);

    private static final Set<ParcelStatus> ALREADY_REMOVED_OR_CANCELED = Set.of(REMOVED_FROM_THE_LIST, CANCELED);

    private static final Set<ParcelStatus> FAILED_STATUS = Set.of(ParcelStatus.FAILED_PICKUP, FAILED_DELIVERY);

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
     * Checks if this status is in final state.
     *
     * @return true if its in final state and false otherwise
     */
    public boolean isFinalState() {
        return FINAL_STATES.contains(this);
    }

    public boolean availableForTask() {
        return !CAN_BE_ADDED_TO_LIST.contains(this);
    }

    public boolean isFailed() {
        return FAILED_STATUS.contains(this);
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
    public boolean isValidTaskItemTransition(ParcelStatus newStatus) {
        return switch (this) {
            case PICKING_UP -> newStatus == PICKED_UP || newStatus == FAILED_PICKUP;
            case DELIVERING -> newStatus == DELIVERED || newStatus == FAILED_DELIVERY;
            default -> false;
        };
    }

    public boolean isValidLifeCycleTransition(ParcelStatus newStatus) {
        return switch (this) {
            case WAITING_FOR_PAYMENT -> newStatus == PICKING_UP;
            case PICKING_UP -> newStatus == PICKED_UP;
            case PICKED_UP -> newStatus == DELIVERING;
            case DELIVERING -> newStatus == DELIVERED;
            default -> false;
        };
    }

    public boolean canBeAddedToTask(TaskType taskType) {
        return switch (taskType) {
            case PICKUP -> this == PICKING_UP;
            case DELIVERY -> this == PICKED_UP;
        };
    }

}
