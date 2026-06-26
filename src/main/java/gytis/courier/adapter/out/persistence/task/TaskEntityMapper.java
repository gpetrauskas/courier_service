package gytis.courier.adapter.out.persistence.task;

import gytis.courier.domain.task.Task;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TaskEntityMapper {
    @Autowired
    protected TaskItemEntityMapper itemMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", source = "taskItems")
    public abstract TaskJpaEntity toEntity(Task domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courier", ignore = true)
    @Mapping(target = "items", ignore = true)
    public abstract void updateWithItems(Task task, @MappingTarget TaskJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "courier", ignore = true)
    public abstract void update(Task task, @MappingTarget TaskJpaEntity entity);

    public Task toDomain(TaskJpaEntity e) {
        return baseRestore(e).build();
    }

    public Task toDomainWithItems(TaskJpaEntity e) {
        return baseRestore(e)
                .items(e.getItems().stream()
                        .map(itemMapper::toDomain)
                        .toList())
                .build();
    }

    @AfterMapping
    public void setTaskOnItems(@MappingTarget TaskJpaEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(i -> i.setTask(entity));
        }
    }

    private Task.Builder baseRestore(TaskJpaEntity e) {
        return Task.restore()
                .id(e.getId())
                .courierId(e.getCourierId())
                .createdByAdminId(e.getCreatedByAdminId())
                .canceledByAdminId(e.getCanceledByAdminId())
                .taskType(e.getTaskType())
                .deliveryStatus(e.getDeliveryStatus())
                .createdAt(e.getCreatedAt())
                .completedAt(e.getCompletedAt());
    }
}

