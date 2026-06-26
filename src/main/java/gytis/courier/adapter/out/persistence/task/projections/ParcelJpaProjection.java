package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.domain.order.ParcelStatus;

public interface ParcelJpaProjection {
    Long getId();
    String getWeight();
    String getDimensions();
    String getContents();
    String getTrackingNumber();
    ParcelStatus getStatus();
}
