package com.example.courier.dto.request.order;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "sectionToEdit", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderSectionUpdateRequest.class, name = "orderSection"),
        @JsonSubTypes.Type(value = ParcelSectionUpdateRequest.class, name = "parcelSection"),
        @JsonSubTypes.Type(value = PaymentSectionUpdateRequest.class, name = "paymentSection"),
        @JsonSubTypes.Type(value = AddressSectionUpdateRequest.class, name = "addressSection")
})
public sealed interface BaseOrderUpdateRequest permits AddressSectionUpdateRequest, OrderSectionUpdateRequest,
        ParcelSectionUpdateRequest, PaymentSectionUpdateRequest {
    Long id();
    String sectionToEdit();

}
