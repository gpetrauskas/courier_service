package gytis.courier.adapter.in.rest.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AddressRequestValidator.class)
public @interface ValidAddressRequest {
    String message() default "Provide either saved address id or full address details";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}