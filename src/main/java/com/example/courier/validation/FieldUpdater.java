package com.example.courier.validation;

import jakarta.annotation.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FieldUpdater {
    public static <T> void updateIfValid(@Nullable T value, Predicate<T> validator, Consumer<T> updater) {
        if (value != null && validator.test(value)) {
            updater.accept(value);
        }
    }

    public static void updateIfPresent(@Nullable String value, Consumer<String> updater) {
        Optional.ofNullable(value)
                .filter(v -> !v.isBlank())
                .ifPresent(updater);
    }

        public static void updateAndTransformIfPresent(@Nullable String value, Function<String, String> transformer,
                                                   Consumer<String> updater) {
        Optional.ofNullable(value)
                .filter(v -> !v.isBlank())
                .map(transformer)
                .ifPresent(updater);
    }

    public static <T, R> void updateAndTransformIfValid(
            @Nullable T value,
            Predicate<T> validator,
            Function<T, R> transformer,
            Consumer<R> updater
    ) {
        if (value != null && validator.test(value)) {
            updater.accept(transformer.apply(value));
        }
    }

    public static void updateIfValidOrThrow(String value, Function<String, String> validatorAndTransformer, Consumer<String> setter) {
        if (value == null || value.isBlank()) return;
        String result = validatorAndTransformer.apply(value);
        setter.accept(result);
    }

    public static void updateBoolean(@Nullable Boolean value, Consumer<Boolean> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }
}
