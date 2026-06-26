package gytis.courier.adapter.in.rest.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    private Set<String> allowed;

    @Override
    public void initialize(ValidEnum validEnum) {
        allowed = Arrays.stream(validEnum.value().getEnumConstants())
                .map(e -> e.name().toUpperCase())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return allowed.contains(value.toUpperCase());
    }
}
