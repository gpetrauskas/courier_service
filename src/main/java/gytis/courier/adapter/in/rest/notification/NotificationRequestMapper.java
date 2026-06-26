package gytis.courier.adapter.in.rest.notification;

import gytis.courier.adapter.in.rest.notification.dto.AdminNotificationQueryRequest;
import gytis.courier.adapter.in.rest.notification.dto.NotificationCreateRequest;
import gytis.courier.adapter.in.rest.notification.dto.NotificationTargetRequest;
import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.query.filter.AdminNotificationQuery;
import gytis.courier.domain.notification.NotificationTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationRequestMapper {
    CreateNotificationCommand toCommand(NotificationCreateRequest request);

    AdminNotificationQuery toQuery(AdminNotificationQueryRequest request);

    NotificationTarget.Broadcast toDomain(NotificationTargetRequest.Broadcast request);
    NotificationTarget.Individual toDomain(NotificationTargetRequest.Individual request);
    default NotificationTarget toTarget(NotificationTargetRequest request) {
        return switch (request) {
            case NotificationTargetRequest.Individual i -> toDomain(i);
            case NotificationTargetRequest.Broadcast b -> toDomain(b);
        };
    }
}
