package gytis.courier.adapter.out.persistence.address.projection;

public interface AddressProjection {
    Long getId();
    String getName();
    String getStreet();
    String getHouseNumber();
    String getFlatNumber();
    String getCity();
    String getPostCode();
    String getPhoneNumber();
}
