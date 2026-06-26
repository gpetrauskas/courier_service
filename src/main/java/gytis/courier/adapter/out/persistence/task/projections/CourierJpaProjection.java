package gytis.courier.adapter.out.persistence.task.projections;

public interface CourierJpaProjection {
    Long getCourierId();
    String getCourierName();
    String getCourierEmail();
    boolean getCourierBlocked();
}
