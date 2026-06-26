package gytis.courier.adapter.in.rest.order;

import gytis.courier.adapter.in.rest.address.dto.AddressRequest;
import gytis.courier.adapter.in.rest.order.dto.OrderAddressSectionUpdateRequest;
import gytis.courier.adapter.in.rest.order.dto.ParcelSectionUpdateRequest;
import gytis.courier.adapter.in.rest.order.dto.PlaceOrderRequest;
import gytis.courier.domain.address.AddressDetails;
import gytis.courier.domain.order.OrderAddressSectionUpdateCommand;
import gytis.courier.domain.order.OrderSectionUpdateCommand;
import gytis.courier.domain.order.ParcelSectionUpdateCommand;
import gytis.courier.domain.order.PlaceOrderCommand;
import gytis.courier.adapter.in.rest.order.dto.OrderSectionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderCommandMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "senderId", source = "request.senderAddress.id")
    @Mapping(target = "recipientId", source = "request.recipientAddress.id")
    @Mapping(target = "sender", source = "request.senderAddress")
    @Mapping(target = "recipient", source = "request.recipientAddress")
    @Mapping(target = "parcelContents", source = "request.parcelDetails.contents")
    @Mapping(target = "weightId", source = "request.parcelDetails.weightId")
    @Mapping(target = "dimensionsId", source = "request.parcelDetails.dimensionsId")
    @Mapping(target = "preferenceId", source = "request.preferenceId")
    PlaceOrderCommand toPlaceOrderCommand(PlaceOrderRequest request, Long userId);

    OrderSectionUpdateCommand toOrderSectionCommand(OrderSectionUpdateRequest request);
    ParcelSectionUpdateCommand toParcelSectionCommand(ParcelSectionUpdateRequest request);
    OrderAddressSectionUpdateCommand toAddressSectionCommand(OrderAddressSectionUpdateRequest request);

    default AddressDetails map(AddressRequest request) {
        if (request == null) return null;
        return AddressDetails.createValidated(
                request.name(),
                request.street(),
                request.houseNumber(),
                request.flatNumber(),
                request.city(),
                request.postCode(),
                request.phoneNumber()
        );
    }
}
