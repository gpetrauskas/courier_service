package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.application.port.in.task.TaskQueryUseCase;
import gytis.courier.domain.event.OrderAddressUpdatedEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderAddressUpdatedHandler {
    private final TaskQueryUseCase taskUseCase;
    private final NotificationCommandUseCase notificationUseCase;

    public OrderAddressUpdatedHandler(TaskQueryUseCase taskUseCase, NotificationCommandUseCase notificationUseCase) {
        this.taskUseCase = taskUseCase;
        this.notificationUseCase = notificationUseCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderAddressUpdate(OrderAddressUpdatedEvent event) {
        taskUseCase.findCourierInfoByParcelId(event.parcelId())
                .ifPresent(info -> notificationUseCase.create(
                        new CreateNotificationCommand(
                                "Order address in your tas was updated",
                                "Task item " + info.taskItemId() + " " + event.selectedAddress() +
                                        " in task " + info.taskId() + " was updated. Be sure you are using updated address!",
                                new NotificationTarget.Individual(info.courierId())
                        )
                ));
    }
}
