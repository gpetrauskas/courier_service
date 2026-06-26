package gytis.courier.domain.order;

import gytis.courier.application.service.order.AddressInput;

public record PlaceOrderCommand(
        Long userId,
        Long senderId,
        Long recipientId,
        AddressInput sender,
        AddressInput recipient,
        String parcelContents,
        Long weightId,
        Long dimensionsId,
        Long preferenceId
) {

}
