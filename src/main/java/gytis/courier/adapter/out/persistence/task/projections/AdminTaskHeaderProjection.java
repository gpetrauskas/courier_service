package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

import java.time.LocalDateTime;

public interface AdminTaskHeaderProjection {
    Long getId();
    TaskType getTaskType();
    DeliveryStatus getDeliveryStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getCompletedAt();
    Long getCourierId();
    String getCourierName();
    String getCourierPhoneNumber();
}
