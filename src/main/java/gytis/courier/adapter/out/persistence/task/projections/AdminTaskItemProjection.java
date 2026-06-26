package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.domain.order.ParcelStatus;

public interface AdminTaskItemProjection {
    Long getId();
    Long getParcelId();
    ParcelStatus getStatus();
    String getDeliveryMethodName();
    String getContents();
    String getWeight();
    String getDimensions();

    String getSenderAddress();
    String getRecipientAddress();
}
