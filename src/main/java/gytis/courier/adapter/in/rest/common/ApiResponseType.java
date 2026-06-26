package gytis.courier.adapter.in.rest.common;

import java.text.MessageFormat;

public enum ApiResponseType {
    MULTIPLE_NOTIFICATIONS_MARK_AS_READ_INFO("info", "No notifications were updated. Please check the IDs"),
    MULTIPLE_NOTIFICATIONS_MARK_AS_READ_SUCCESS("success", "Marked {0} of {1} notifications as read"),

    USER_REGISTRATION_SUCCESS("success", "UserJpaEntity registered successfully"),
    PERSON_EMAIL_EXISTS("error", "Email already registered"),
    PERSON_PASSWORD_WEAK("error", "Password does not meet requirements"),
    COURIER_REGISTRATION_ADMIN_ONLY("error", "Only admins can register couriers"),
    COURIER_REGISTRATION_SUCCESS("success", "Courier registered by admin {0}"),
    DELIVERY_OPTION_UPDATE_SUCCESS("success", "Option id: {0} updated successfully"),

    NOTIFICATIONS_DELETE_SUCCESS("success", "Deleted {0} of {1} notifications"),
    NOTIFICATIONS_DELETE_INFO("info", "No notifications found to be deleted"),
    SINGLE_NOTIFICATION_DELETE_SUCCESS("success", "Notification was deleted successfully"),
    SINGLE_NOTIFICATION_DELETE_INFO("info", "Nothing to delete"),


    NOTIFICATIONS_DELETE_SUCCESS_ADMIN("success", "Notification id {0} deleted, along with {1} person notification(s)"),
    //shared
    NO_CHANGES_DETECTED("info", "No changes detected for {0} with id {1}"),
    CREATION_SUCCESS("success", "{0} was created successfully"),
    DELETION_SUCCESS("success", "{0} with id {1} was deleted successfully");

    private final String status;
    private final String message;

    ApiResponseType(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse apiResponse() {
        return new ApiResponse(status, message);
    }

    public ApiResponse withParams(Object... args) {
        try {
            String formattedMessage = MessageFormat.format(this.message, args);
            return new ApiResponse(this.status, formattedMessage);
        } catch (IllegalArgumentException e) {
            return new ApiResponse(this.status, this.message);
        }
    }

}
