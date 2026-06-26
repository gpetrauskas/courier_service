package gytis.courier.application.service.task;

import gytis.courier.application.port.out.task.AdminTaskQueryPort;
import gytis.courier.domain.task.TaskAssignmentPolicy;
import org.springframework.stereotype.Component;

@Component
public class TaskAssignmentPolicyImpl implements TaskAssignmentPolicy {
    private final AdminTaskQueryPort queryPort;

    public TaskAssignmentPolicyImpl(AdminTaskQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public void ensureCourierIsAvailable(Long courierId) {
        if (queryPort.existsActiveByCourierId(courierId)) {
            throw new IllegalStateException("Courier already has an active task");
        }
    }

}
