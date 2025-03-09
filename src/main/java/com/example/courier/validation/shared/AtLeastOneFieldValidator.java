package com.example.courier.validation.shared;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {

    private List<String> ignoreList;

    @Override
    public void initialize(AtLeastOneField constraintAnnotation) {
        this.ignoreList = Arrays.asList(constraintAnnotation.ignoredFields());
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return false;
        }

        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (ignoreList.contains(field.getName())) {
                continue;
            }

            try {
                Object value = field.get(dto);
                if (value != null && !isBlank(value)) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field during validation", e);
            }
        }

        return false;
    }

    private boolean isBlank(Object value) {
        if (value instanceof CharSequence) {
            return ((CharSequence) value).toString().trim().isEmpty();
        }
        return false;
    }
}
