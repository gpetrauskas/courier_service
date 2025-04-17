package com.example.courier.common;

import com.example.courier.dto.ApiResponseDTO;

import java.text.MessageFormat;

public enum ApiResponseType {
    MULTIPLE_NOTIFICATIONS_MARK_AS_READ_INFO("info", "No notifications were updated. Please check the IDs"),
    MULTIPLE_NOTIFICATIONS_MARK_AS_READ_SUCCESS("success", "Marked {0} of {1} notifications as read"),
    SINGLE_NOTIFICATION_MARK_AS_READ_INFO("info", "Notification already marked as read"),
    SINGLE_NOTIFICATION_MARK_AS_READ_SUCCESS("success", "Notification marked as read successfully"),

    MULTIPLE_NOTIFICATIONS_DELETE_SUCCESS("succes", "Deleted {0} of {1} notifications"),
    MULTIPLE_NOTIFICATIONS_DELEte_INFO("info", "No notifications found to be deleted"),
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
        String formatedMessage = MessageFormat.format(this.message, args);
        return new ApiResponseDTO(this.status, formatedMessage);
    }

}
