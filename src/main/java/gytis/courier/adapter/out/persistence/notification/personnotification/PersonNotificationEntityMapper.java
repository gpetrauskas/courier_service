package gytis.courier.adapter.out.persistence.notification.personnotification;

import gytis.courier.domain.personnotification.PersonNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonNotificationEntityMapper {
    PersonNotificationJpaEntity toEntity(PersonNotification domain);
    PersonNotification toDomain(PersonNotificationJpaEntity entity);

    @Mapping(target = "id.personId", ignore = true)
    @Mapping(target = "id.notificationId", ignore = true)
    void updateEntity(PersonNotification domain, @MappingTarget PersonNotificationJpaEntity entity);
}
