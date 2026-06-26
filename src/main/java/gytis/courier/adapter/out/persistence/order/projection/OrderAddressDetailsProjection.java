package gytis.courier.adapter.out.persistence.order.projection;

public interface OrderAddressDetailsProjection {
    String getName();
    String getStreet();
    String getHouseNumber();
    String getFlatNumber();
    String getCity();
    String getPostCode();
    String getPhoneNumber();
}
