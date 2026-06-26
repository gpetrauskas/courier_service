package gytis.courier.adapter.out.persistence.notification;

import gytis.courier.adapter.out.persistence.notification.projection.NotificationProjection;
import gytis.courier.application.readmodel.notification.NotificationReadModel;
import gytis.courier.domain.notification.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationEntityMapper {
    NotificationJpaEntity toEntity(Notification domain);
    Notification toDomain(NotificationJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(Notification domain, @MappingTarget NotificationJpaEntity entity);


    NotificationReadModel toReadModel(NotificationProjection projection);

}
