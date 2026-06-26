package gytis.courier.adapter.out.persistence.parcel.projection;

import gytis.courier.domain.order.ParcelStatus;

public interface ParcelProjection {
        Long getId();
        String getWeightName();
        String getDimensionsName();
        String getContents();
        String getTrackingNumber();
        ParcelStatus getStatus();
}
