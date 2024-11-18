package com.example.courier.common;

public enum Role {
    ADMIN,
    USER,
    COURIER;

    public static boolean isValidRole(String role) {
        for (Role roleName : values()) {
            if (roleName.name().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }
}
