package gytis.courier.adapter.in.rest.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTicketPriorityValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTicketPriority {
    String message() default "Invalid ticket priority";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
