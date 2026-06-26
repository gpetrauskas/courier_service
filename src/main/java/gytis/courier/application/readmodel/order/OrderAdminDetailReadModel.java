package gytis.courier.application.readmodel.order;

import gytis.courier.application.readmodel.address.AddressReadModel;
import gytis.courier.application.readmodel.parcel.ParcelReadModel;
import gytis.courier.application.readmodel.payment.AdminPaymentSummaryReadModel;
import gytis.courier.application.readmodel.person.UserReadModel;
import gytis.courier.domain.order.OrderStatus;

import java.time.LocalDateTime;

public record OrderAdminDetailReadModel(
        Long id,
        String deliveryMethodName,
        OrderStatus status,
        LocalDateTime createDate,
        AdminPaymentSummaryReadModel payment,
        UserReadModel user,
        AddressReadModel sender,
        AddressReadModel recipient,
        ParcelReadModel parcel
) {
}
