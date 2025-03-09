package com.example.courier.validation.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneFieldValidator.class)
public @interface AtLeastOneField {
    String message() default "At least one field (other than ignored fields) must be provided.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] ignoredFields() default { "id", "sectionToEdit" };
}
