package gytis.courier.adapter.in.rest.task.dto;

import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TaskQueryRequestMapper {
    @Mapping(target = "taskType", source = "taskType", qualifiedByName = "mapTaskType")
    @Mapping(target = "deliveryStatus", source = "deliveryStatus", qualifiedByName = "mapDeliveryStatus")
    AdminTaskQueryFilter toQueryFilter(AdminTaskFilterRequest request);

    @Named("mapTaskType")
    default TaskType mapTaskType(String type) {
        if (type == null) return null;
        return TaskType.fromString(type)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task type: " + type));
    }

    @Named("mapDeliveryStatus")
    default DeliveryStatus mapDeliveryStatus(String status) {
        if (status == null) return null;
        return DeliveryStatus.fromString(status)
                .orElseThrow(() -> new IllegalArgumentException("Invalid delivery status: " + status));
    }
}
