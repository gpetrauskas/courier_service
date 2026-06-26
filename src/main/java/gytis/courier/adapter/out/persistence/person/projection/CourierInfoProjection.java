package gytis.courier.adapter.out.persistence.person.projection;

public interface CourierInfoProjection {
    String getName();
    String getEmail();
    boolean hasActiveTask();
}
