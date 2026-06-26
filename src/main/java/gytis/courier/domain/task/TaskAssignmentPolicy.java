package gytis.courier.domain.task;

public interface TaskAssignmentPolicy {
    void ensureCourierIsAvailable(Long courierId);
}
