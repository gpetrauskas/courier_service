package gytis.courier.adapter.out.persistence.task.projections;

public interface AdminTaskStatsProjection {
    Long getCreatedToday();
    Long getCompletedToday();
    Double getAverageDurationInMinutes();
}
