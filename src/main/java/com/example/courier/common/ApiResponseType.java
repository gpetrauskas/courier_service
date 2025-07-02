package com.example.courier.common;

import com.example.courier.dto.ApiResponseDTO;

import java.text.MessageFormat;

public enum ApiResponseType {
    MULTIPLE_NOTIFICATIONS_MARK_AS_READ_INFO("info", "No notifications were updated. Please check the IDs"),
    MULTIPLE_NOTIFICATIONS_MARK_AS_READ_SUCCESS("success", "Marked {0} of {1} notifications as read"),
    USER_REGISTRATION_SUCCESS("success", "User registered successfully"),
    PERSON_EMAIL_EXISTS("error", "Email already registered"),
    PERSON_PASSWORD_WEAK("error", "Password does not meet requirements"),
    COURIER_REGISTRATION_ADMIN_ONLY("error", "Only admins can register couriers"),
    COURIER_REGISTRATION_SUCCESS("success", "Courier registered by admin {0}"),

    NOTIFICATIONS_DELETE_SUCCESS_ADMIN("success", "Notification was deleted successfully"),
    NOTIFICATIONS_DELETE_SUCCESS("success", "Deleted {0} of {1} notifications"),
    NOTIFICATIONS_DELETE_INFO("info", "No notifications found to be deleted"),
    SINGLE_NOTIFICATION_DELETE_SUCCESS("success", "Notification was deleted successfully"),
    SINGLE_NOTIFICATION_DELETE_INFO("info", "Nothing to delete");

    private final String status;
    private final String message;

    ApiResponseType(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponseDTO apiResponseDTO() {
        return new ApiResponseDTO(status, message);
    }

    public ApiResponseDTO withParams(Object... args) {
        try {
            String formattedMessage = MessageFormat.format(this.message, args);
            return new ApiResponseDTO(this.status, formattedMessage);
        } catch (IllegalArgumentException e) {
            return new ApiResponseDTO(this.status, this.message);
        }
    }

}
