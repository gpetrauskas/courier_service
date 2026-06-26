package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.domain.order.ParcelStatus;

public interface CourierTaskItemProjection {
    Long getId();
    ParcelStatus getParcelStatus();
    String getDeliveryMethodName();
    String getContents();
    String getWeight();
    String getDimensions();
    String getSenderAddress();
    String getRecipientAddress();
    String getSenderContacts();
    String getRecipientContacts();
}
