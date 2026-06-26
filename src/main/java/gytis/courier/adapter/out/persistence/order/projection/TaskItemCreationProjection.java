package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.domain.order.ParcelStatus;

public interface TaskItemCreationProjection {
    Long getParcelId();
    ParcelStatus getParcelStatus();
    Long getSenderAddressId();
    Long getRecipientAddressId();
    String getContents();
    String getDeliveryMethodName();
}
