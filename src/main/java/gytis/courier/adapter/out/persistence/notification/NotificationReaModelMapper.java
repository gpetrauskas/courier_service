package gytis.courier.adapter.out.persistence.notification;

import gytis.courier.adapter.out.persistence.notification.projection.PersonNotificationProjection;
import gytis.courier.application.readmodel.notification.NotificationReadModel;
import gytis.courier.application.readmodel.personnotification.PersonNotificationReadModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationReaModelMapper {
    @Mapping(target = "id", source = "notificationId")
    @Mapping(target = "isRead", source = "read")
    PersonNotificationReadModel toPersonReadModel(PersonNotificationProjection projection);

    NotificationReadModel toReadModel(NotificationJpaEntity entity);
}
