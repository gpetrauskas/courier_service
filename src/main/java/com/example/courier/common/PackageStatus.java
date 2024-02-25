package com.example.courier.common;

public enum PackageStatus {
    WAITING_FOR_PAYMENT,
    PICKING_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELED,
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
