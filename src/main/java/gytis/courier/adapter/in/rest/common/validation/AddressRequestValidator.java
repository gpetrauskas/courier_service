package gytis.courier.adapter.in.rest.common.validation;

import gytis.courier.adapter.in.rest.address.dto.AddressRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddressRequestValidator implements ConstraintValidator<ValidAddressRequest, AddressRequest> {

    @Override
    public boolean isValid(AddressRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        boolean hasId = request.id() != null;
        boolean hasDetails = request.city() != null && !request.city().isBlank()
                && request.street() != null && !request.street().isBlank()
                && request.houseNumber() != null && !request.houseNumber().isBlank()
                && request.name() != null && !request.name().isBlank()
                && request.phoneNumber() != null && !request.phoneNumber().isBlank()
                && request.postCode() != null && !request.postCode().isBlank();

        return hasId ^ hasDetails;
    }
}
