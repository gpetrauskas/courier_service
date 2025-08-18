package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ProviderType {
    CREDIT_CARD,
    PAYPAL,
    UNKNOWN;

    private static final Set<String> PROVIDER_TYPES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private static void isTypeSupported(String type) {
        if (type != null && !PROVIDER_TYPES.contains(type)) {
            throw new IllegalArgumentException("Provider type is not supported");
        }
    }
}
