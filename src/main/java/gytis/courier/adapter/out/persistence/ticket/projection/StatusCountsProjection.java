package gytis.courier.adapter.out.persistence.ticket.projection;

public interface StatusCountsProjection {
    Long getOpenStatus();
    Long getInProgressStatus();
    Long getResolvedStatus();
}
