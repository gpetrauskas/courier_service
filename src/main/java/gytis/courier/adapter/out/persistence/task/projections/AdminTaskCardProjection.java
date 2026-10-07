package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

public interface AdminTaskCardProjection {
    TaskType getTaskType();
    DeliveryStatus getDeliveryStatus();
    Long getCount();
}
