/*
package com.example.courier.application.service.order;

import projection.order.persistence.out.adapter.gytis.courier.OrderDetailProjection;
import address.readmodel.application.gytis.courier.AddressReadModel;
import order.readmodel.application.gytis.courier.OrderAdminDetailReadModel;
import parcel.readmodel.application.gytis.courier.ParcelReadModel;
import person.readmodel.application.gytis.courier.UserReadModel;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailAssembler {
    public OrderAdminDetailReadModel from(OrderDetailProjection p) {
        return new OrderAdminDetailReadModel(
                p.getId(),
                p.getPaymentId(),
                p.getDeliveryMethod().getName(),
                p.getStatus(),
                p.getCreateDate(),

                new UserReadModel(
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getUser().getEmail(),
                        p.getUser().getPhoneNumber(),
                        p.getUser().isBlocked(),
                        p.getUser().isDeleted(),
                        p.getUser().getDeletedDate()
                ),

                new AddressReadModel(
                        p.getSenderAddress().getId(),
                        p.getSenderAddress().getDetails().getName(),
                        p.getSenderAddress().getDetails().getStreet(),
                        p.getSenderAddress().getDetails().getHouseNumber(),
                        p.getSenderAddress().getDetails().getFlatNumber(),
                        p.getSenderAddress().getDetails().getCity(),
                        p.getSenderAddress().getDetails().getPostCode(),
                        p.getSenderAddress().getDetails().getPhoneNumber()
                ),

                new AddressReadModel(
                        p.getRecipientAddress().getId(),
                        p.getRecipientAddress().getDetails().getName(),
                        p.getRecipientAddress().getDetails().getStreet(),
                        p.getRecipientAddress().getDetails().getHouseNumber(),
                        p.getRecipientAddress().getDetails().getFlatNumber(),
                        p.getRecipientAddress().getDetails().getCity(),
                        p.getRecipientAddress().getDetails().getPostCode(),
                        p.getRecipientAddress().getDetails().getPhoneNumber()
                ),

                new ParcelReadModel(
                        p.getParcel().getId(),
                        p.getParcel().getContents(),
                        p.getParcel().getWeight().getName(),
                        p.getParcel().getDimensions().getName(),
                        p.getParcel().getTrackingNumber(),
                        p.getParcel().getStatus()
                )
        );
    }
}
*/
