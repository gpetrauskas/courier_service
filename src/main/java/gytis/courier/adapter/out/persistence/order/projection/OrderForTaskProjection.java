package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.domain.order.ParcelStatus;

import java.time.LocalDateTime;

public interface OrderForTaskProjection {
    Long getId();
    Long getParcelId();
    ParcelStatus getParcelStatus();
    String getContents();
    int getFailuresCount();
    LocalDateTime getCreateDate();
    String getDeliveryMethodName();
    String getWeight();
    String getDimensions();
    String getName();
    String getPhoneNumber();
    String getStreet();
    String getHouseNumber();
    String getFlatNumber();
    String getCity();
    String getPostCode();
}
