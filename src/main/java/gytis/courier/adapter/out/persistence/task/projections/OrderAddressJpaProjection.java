package gytis.courier.adapter.out.persistence.task.projections;

public interface OrderAddressJpaProjection {
    Long getId();
    String getCity();
    String getStreet();
    String getHouseNumber();
    String getFlatNumber();
    String getPhoneNumber();
    String getPostalCode();
    String getName();
}
