package gytis.courier.application.event;

import gytis.courier.application.port.in.parcel.ParcelCommandUseCase;
import gytis.courier.domain.event.TaskCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class TaskCompletedEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TaskCompletedEventHandler.class);
    private final ParcelCommandUseCase useCase;

    public TaskCompletedEventHandler(ParcelCommandUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCompletedTask(TaskCompletedEvent event) {
        useCase.handleTaskCompleted(event.success(), event.failed());
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void logCompletedTask(TaskCompletedEvent event) {
        log.info("Task ID: {} was completed at {}", event.taskId(), event.completedAt());
    }
}
