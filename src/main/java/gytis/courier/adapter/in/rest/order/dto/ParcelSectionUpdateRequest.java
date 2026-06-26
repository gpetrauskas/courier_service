package gytis.courier.adapter.in.rest.order.dto;

import gytis.courier.adapter.in.rest.common.validation.AtLeastOneField;

@AtLeastOneField
public record ParcelSectionUpdateRequest(
        String status,
        String contents
) {
}
