package gytis.courier.adapter.out.persistence.order.projection;

public interface AddressDetailsProjection {
    String getCity();
    String getStreet();
    String getHouseNumber();
    String getFlatNumber();
    String getPhoneNumber();
    String getPostCode();
    String getName();
}
