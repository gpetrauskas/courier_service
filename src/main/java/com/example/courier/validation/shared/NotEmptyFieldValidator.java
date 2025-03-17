package com.example.courier.validation.shared;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Map;

public class NotEmptyFieldValidator implements ConstraintValidator<NotEmptyField, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null ) {
            return false;
        }

        if (value instanceof String str) {
            return !str.trim().isEmpty();
        }

        if (value instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }

        if (value instanceof Map<?,?> map) {
            return !map.isEmpty();
        }

        if (value instanceof Object[] array) {
            return array.length > 0;
        }

        return true;
    }
}
