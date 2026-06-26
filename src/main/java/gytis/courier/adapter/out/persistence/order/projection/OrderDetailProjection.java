package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.adapter.out.persistence.parcel.projection.ParcelProjection;
import gytis.courier.adapter.out.persistence.person.projection.UserProjection;
import gytis.courier.domain.order.OrderStatus;

import java.time.LocalDateTime;

public interface OrderDetailProjection {
    Long getId();
    OrderStatus getStatus();
    LocalDateTime getCreateDate();
    UserProjection getUser();
    OrderAddressProjection getSenderAddress();
    OrderAddressProjection getRecipientAddress();
    ParcelProjection getParcel();
    String getDeliveryMethodName();
}
