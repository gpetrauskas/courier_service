package gytis.courier.adapter.in.rest.order.dto;

import gytis.courier.adapter.in.rest.common.validation.AtLeastOneField;

@AtLeastOneField
public record OrderAddressSectionUpdateRequest(
        String selectedAddress,
        Long id,
        String city,
        String street,
        String houseNumber,
        String flatNumber,
        String phoneNumber,
        String postCode,
        String name
) {
}
