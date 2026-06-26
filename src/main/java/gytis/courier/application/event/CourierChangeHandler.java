package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.domain.event.CourierChangeEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CourierChangeHandler {
    private final NotificationCommandUseCase useCase;

    public CourierChangeHandler(NotificationCommandUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourierChange(CourierChangeEvent event) {
        //sendNotification(event.taskId(), event.newCourier());
    }

    private void sendNotification(Long taskId, Long courierId) {
        useCase.create(
                new CreateNotificationCommand(
                        "Task assigned",
                        "Task ID: " + taskId + " was assigned to you",
                        new NotificationTarget.Individual(courierId)
                )
        );
    }
}
