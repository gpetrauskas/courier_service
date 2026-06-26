package gytis.courier.adapter.out.persistence.delivery.projection;

import java.math.BigDecimal;

public interface DeliveryOptionProjection {
    Long getId();
    String getName();
    String getDescription();
    BigDecimal getPrice();
    boolean isDisabled();
}