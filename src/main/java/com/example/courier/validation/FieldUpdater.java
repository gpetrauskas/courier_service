package com.example.courier.validation;

import jakarta.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FieldUpdater {
    public static <T> void updateIfValid(@Nullable T value, Predicate<T> validator, Consumer<T> updater) {
        if (value != null && validator.test(value)) {
            updater.accept(value);
        }
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

    public static void updateBoolean(@Nullable Boolean value, Consumer<Boolean> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }
}
