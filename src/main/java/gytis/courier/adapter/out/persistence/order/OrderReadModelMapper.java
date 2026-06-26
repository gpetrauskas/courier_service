package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.address.AddressReadModelMapper;
import gytis.courier.adapter.out.persistence.common.AddressFormatter;
import gytis.courier.adapter.out.persistence.order.projection.OrderDetailProjection;
import gytis.courier.adapter.out.persistence.order.projection.OrderForTaskProjection;
import gytis.courier.adapter.out.persistence.order.projection.OrderListProjection;
import gytis.courier.adapter.out.persistence.order.projection.PaymentProjection;
import gytis.courier.adapter.out.persistence.parcel.ParcelReadModelMapper;
import gytis.courier.adapter.out.persistence.payment.PaymentReadModelMapper;
import gytis.courier.adapter.out.persistence.person.user.PersonInfoReadModelMapper;
import gytis.courier.application.readmodel.order.AdminOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderUserDetailReadModel;
import gytis.courier.application.readmodel.order.UserOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderAdminDetailReadModel;
import gytis.courier.application.readmodel.order.OrderForTaskReadModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        AddressReadModelMapper.class,
        ParcelReadModelMapper.class,
        PersonInfoReadModelMapper.class,
        PaymentReadModelMapper.class
})
public interface OrderReadModelMapper {
    AdminOrderListReadModel toAdminList(OrderListProjection projection);
    UserOrderListReadModel toUserList(OrderListProjection projection);

    @Mapping(target = "sender", source = "projection.senderAddress")
    @Mapping(target = "recipient", source = "projection.recipientAddress")
    @Mapping(target = "id", source = "projection.id")
    @Mapping(target = "deliveryMethodName", source = "projection.deliveryMethodName")
    @Mapping(target = "status", source = "projection.status")
    @Mapping(target = "createDate", source = "projection.createDate")
    @Mapping(target = "user", source = "projection.user")
    @Mapping(target = "parcel", source = "projection.parcel")
    @Mapping(target = "payment", source = "paymentProjection")
    OrderAdminDetailReadModel toAdminDetailed(OrderDetailProjection projection, PaymentProjection paymentProjection);

    default OrderUserDetailReadModel toUserDetailed(OrderDetailProjection p) {
        var s = p.getSenderAddress().getDetailsJpa();
        var r = p.getRecipientAddress().getDetailsJpa();
        return new OrderUserDetailReadModel(
                p.getId(),
                p.getDeliveryMethodName(),
                p.getStatus(),
                p.getCreateDate(),
                AddressFormatter.toFullAddress(
                        s.getStreet(), s.getHouseNumber(), s.getFlatNumber(), s.getCity(), s.getPostCode()
                ),
                AddressFormatter.toFullAddress(
                        r.getStreet(), r.getHouseNumber(), r.getFlatNumber(), r.getCity(), r.getPostCode()
                ),
                p.getParcel().getContents(),
                p.getParcel().getWeightName(),
                p.getParcel().getDimensionsName(),
                p.getParcel().getTrackingNumber(),
                p.getParcel().getStatus()
        );
    }


    @Mapping(target = "fullAddress", expression = "java(formatFullAddress(o))")
    @Mapping(target = "customerContacts",
            expression = "java(formatCustomerContacts(o.getName(), o.getPhoneNumber()))")
    OrderForTaskReadModel toReadModel(OrderForTaskProjection o);

    default String formatCustomerContacts(String name, String phoneNumber) {
        return name + " " + phoneNumber;
    }

    default String formatFullAddress(OrderForTaskProjection o) {
        return AddressFormatter.toFullAddress(
                o.getStreet(),
                o.getHouseNumber(),
                o.getFlatNumber(),
                o.getCity(),
                o.getPostCode()
        );
    }
}
