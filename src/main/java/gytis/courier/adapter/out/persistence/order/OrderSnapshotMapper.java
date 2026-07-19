package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.order.projection.TaskItemCreationProjection;
import gytis.courier.domain.task.TaskItemCreationSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderSnapshotMapper {
    @Mapping(target = "status", source = "projection.parcelStatus")
    TaskItemCreationSnapshot toSnapshot(TaskItemCreationProjection projection);
}
