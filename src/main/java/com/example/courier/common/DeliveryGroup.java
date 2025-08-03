package com.example.courier.common;

import java.util.Arrays;

public enum DeliveryGroup {
    SIZE("size"),
    WEIGHT("weight"),
    PREFERENCE("preference");

    private final String keyword;

    DeliveryGroup(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public static DeliveryGroup determineGroupFromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Delivery method cannot be null");
        }

        String lower = name.toLowerCase();
        return Arrays.stream(values())
                .filter(group -> lower.contains(group.keyword))
                .findFirst()
                .orElse(PREFERENCE);
    }
}
