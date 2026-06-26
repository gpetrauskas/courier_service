package gytis.courier.adapter.in.rest.person.dto;

import gytis.courier.adapter.in.rest.common.validation.AtLeastOneField;

@AtLeastOneField
public record UserSelfEditRequest(
        String phoneNumber,
        Long defaultAddressId,
        Boolean subscribed
) {
}
