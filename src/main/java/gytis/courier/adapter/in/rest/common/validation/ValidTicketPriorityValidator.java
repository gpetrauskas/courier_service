package gytis.courier.adapter.in.rest.common.validation;

import gytis.courier.domain.ticket.TicketPriority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTicketPriorityValidator implements ConstraintValidator<ValidTicketPriority, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return TicketPriority.isValidPriority(s);
    }
}
