package com.example.courier.common;

public enum PackageStatus {
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
        for (PackageStatus packageStatus : values()) {
            if (packageStatus.name().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}
