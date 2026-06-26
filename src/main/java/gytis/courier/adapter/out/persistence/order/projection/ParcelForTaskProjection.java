package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.domain.order.ParcelStatus;

public interface ParcelForTaskProjection {
    Long getId();
    String getContents();
    String getWeightName();
    String getDimensionsName();
    ParcelStatus getStatus();
    boolean isAssigned();
}
