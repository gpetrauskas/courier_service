package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.domain.notification.NotificationTargetType;
import gytis.courier.domain.event.TaskCanceledEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TaskCanceledHandler {
    private final NotificationCommandUseCase useCase;

    public TaskCanceledHandler(NotificationCommandUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TaskCanceledEvent event) {
        useCase.create(new CreateNotificationCommand(
                "Task ID: " + event.taskId() + " canceled",
                "Task was canceled by admin ID: " + event.adminId(),
                new NotificationTarget.Broadcast(NotificationTargetType.ADMIN)
        ));
    }
}
