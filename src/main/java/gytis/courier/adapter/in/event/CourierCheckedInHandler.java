package gytis.courier.adapter.in.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.application.port.in.person.CourierCommandUseCase;
import gytis.courier.domain.notification.NotificationTargetType;
import gytis.courier.domain.event.CourierCheckedInEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CourierCheckedInHandler {
    private final NotificationCommandUseCase notificationUseCase;
    private final CourierCommandUseCase courierUseCase;

    public CourierCheckedInHandler(NotificationCommandUseCase notificationUseCase, CourierCommandUseCase courierUseCase) {
        this.notificationUseCase = notificationUseCase;
        this.courierUseCase = courierUseCase;
    }

    @EventListener
    public void onCourierCheckIn(CourierCheckedInEvent event) {
        courierUseCase.deactivate(event.courierId());
        notificationUseCase.create(new CreateNotificationCommand(
                "Courier " + event.courierId() + " checked in.",
                "Courier checked in: Task ID: " + event.taskId() + ", Courier ID: " + event.courierId(),
                new NotificationTarget.Broadcast(NotificationTargetType.ADMIN)
        ));
    }
}
