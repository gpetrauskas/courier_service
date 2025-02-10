package com.example.courier.common;

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
    NOT_SHIPPED;

    public static boolean isValidStatus(String status) {
        for (ParcelStatus parcelStatus : values()) {
            if (parcelStatus.name().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}
