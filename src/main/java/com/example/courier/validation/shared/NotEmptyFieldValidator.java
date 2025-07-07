package com.example.courier.validation.shared;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Map;

public class NotEmptyFieldValidator implements ConstraintValidator<NotNullOrEmpty, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        return switch (value) {
            case null -> false;
            case String str -> !str.trim().isEmpty();
            case Collection<?> collection -> !collection.isEmpty();
            case Map<?, ?> map -> !map.isEmpty();
            case Object[] array -> array.length > 0;
            default -> true;
        };
    }
}