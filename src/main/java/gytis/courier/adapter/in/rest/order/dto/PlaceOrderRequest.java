package gytis.courier.adapter.in.rest.order.dto;

import gytis.courier.adapter.in.rest.address.dto.AddressRequest;

public record PlaceOrderRequest(
        AddressRequest senderAddress,
        AddressRequest recipientAddress,
        ParcelRequest parcelDetails,
        Long preferenceId
) {
}
