package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.application.port.in.person.CourierCommandUseCase;
import gytis.courier.domain.event.TaskAssignedEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class TaskAssignedHandler {
    private final NotificationCommandUseCase notificationUseCase;
    private final CourierCommandUseCase courierUseCase;

    public TaskAssignedHandler(NotificationCommandUseCase notificationUseCase, CourierCommandUseCase courierUseCase) {
        this.notificationUseCase = notificationUseCase;
        this.courierUseCase = courierUseCase;
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskAssigned(TaskAssignedEvent event) {
        courierUseCase.activate(event.courierId());
        notificationUseCase.create(
                new CreateNotificationCommand(
                        "Task was assigned",
                        "Task was assigned to you, please be ready to leave asap",
                        new NotificationTarget.Individual(event.courierId())
                )
        );
    }
}
