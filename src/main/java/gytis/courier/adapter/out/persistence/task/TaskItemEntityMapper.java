package gytis.courier.adapter.out.persistence.task;

import gytis.courier.domain.task.TaskItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class TaskItemEntityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "senderAddress", ignore = true)
    @Mapping(target = "recipientAddress", ignore = true)
    public abstract TaskItemJpaEntity toEntity(TaskItem domain);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "senderAddress", ignore = true)
    @Mapping(target = "recipientAddress", ignore = true)
    public abstract void update(TaskItem domain, @MappingTarget TaskItemJpaEntity entity);

    public TaskItem toDomain(TaskItemJpaEntity entity) {
        return TaskItem.restore()
                .id(entity.getId())
                .parcelId(entity.getParcelId())
                .parcelStatus(entity.getParcelStatus())
                .senderAddressId(entity.getSenderAddressId())
                .recipientAddressId(entity.getRecipientAddressId())
                .deliveryMethodName(entity.getDeliveryMethodName())
                .contents(entity.getContents())
                .build();
    }
}
