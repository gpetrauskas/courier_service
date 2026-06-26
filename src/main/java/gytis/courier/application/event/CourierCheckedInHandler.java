package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.application.port.in.person.CourierCommandUseCase;
import gytis.courier.domain.notification.NotificationTargetType;
import gytis.courier.domain.event.CourierCheckedInEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CourierCheckedInHandler {
    private final NotificationCommandUseCase notificationUseCase;
    private final CourierCommandUseCase courierUseCase;

    public CourierCheckedInHandler(NotificationCommandUseCase notificationUseCase, CourierCommandUseCase courierUseCase) {
        this.notificationUseCase = notificationUseCase;
        this.courierUseCase = courierUseCase;
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourierCheckIn(CourierCheckedInEvent event) {
        courierUseCase.deactivate(event.courierId());
        notificationUseCase.create(new CreateNotificationCommand(
                "Courier " + event.courierId() + " checked in.",
                "Courier checked in: Task ID: " + event.taskId() + ", Courier ID: " + event.courierId(),
                new NotificationTarget.Broadcast(NotificationTargetType.ADMIN)
        ));
    }
}
