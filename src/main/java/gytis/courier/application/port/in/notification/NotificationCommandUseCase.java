package gytis.courier.application.port.in.notification;

import gytis.courier.application.command.CreateNotificationCommand;

public interface NotificationCommandUseCase {
    int delete(Long id);
    void create(CreateNotificationCommand command);
}
