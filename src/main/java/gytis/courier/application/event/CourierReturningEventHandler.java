package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.domain.notification.NotificationTargetType;
import gytis.courier.domain.event.CourierReturningEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CourierReturningEventHandler {
    private final NotificationCommandUseCase commandUseCase;

    public CourierReturningEventHandler(NotificationCommandUseCase commandUseCase) {
        this.commandUseCase = commandUseCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourierReturn(CourierReturningEvent event) {
        commandUseCase.create(
                new CreateNotificationCommand(
                        "Courier " + event.courierId() + " returning",
                        "Courier " + event.courierId() + " finished his task " + event.taskId() + " and now returning to base",
                        new NotificationTarget.Broadcast(NotificationTargetType.ADMIN)
                )
        );
    }
}
