package com.example.courier.dto.request.person;

import com.example.courier.validation.shared.AtLeastOneField;

@AtLeastOneField
public record UserEditDTO(
        String phoneNumber,
        Long defaultAddressId,
        Boolean subscribed
) {
}
