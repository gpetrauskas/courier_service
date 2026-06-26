package gytis.courier.adapter.out.persistence.order.projection;

public interface OrderAddressProjection {
    Long getId();
    OrderAddressDetailsProjection getDetailsJpa();
}
