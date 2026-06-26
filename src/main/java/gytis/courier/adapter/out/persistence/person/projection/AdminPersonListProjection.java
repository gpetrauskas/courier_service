package gytis.courier.adapter.out.persistence.person.projection;

public interface AdminPersonListProjection {
    Long getId();
    String getName();
    String getEmail();
    boolean isBlocked();
    boolean isDeleted();
}
