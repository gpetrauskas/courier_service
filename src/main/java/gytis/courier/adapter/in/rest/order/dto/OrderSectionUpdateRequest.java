package gytis.courier.adapter.in.rest.order.dto;

import gytis.courier.adapter.in.rest.common.validation.AtLeastOneField;

@AtLeastOneField
public record OrderSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String status,
        String deliveryMethodName
) {
}
