package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.order.projection.TaskItemCreationProjection;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskItemCreationSnapshot;
import gytis.courier.domain.task.TaskType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderSnapshotMapper {
    @Mapping(target = "status", expression = "java(changeStatusIfNeeded(projection.getParcelStatus()))")
    TaskItemCreationSnapshot toSnapshot(TaskItemCreationProjection projection);

    default ParcelStatus changeStatusIfNeeded(ParcelStatus status) {
        if (status == ParcelStatus.PICKED_UP) {
            return ParcelStatus.DELIVERING;
        }
        return status;
    }
}
