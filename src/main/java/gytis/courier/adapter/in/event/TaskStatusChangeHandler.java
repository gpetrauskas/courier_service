package gytis.courier.adapter.in.event;

import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.application.port.out.websocket.WebSocketPublisherPort;
import gytis.courier.domain.event.TaskStatusChangeEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class TaskStatusChangeHandler {
    private final WebSocketPublisherPort wsPort;

    public TaskStatusChangeHandler(WebSocketPublisherPort wsPort) {
        this.wsPort = wsPort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishStatusChanged(TaskStatusChangeEvent event) {
        System.out.println("status handler");
        wsPort.broadcast(WebSocketDestinations.ADMIN_TASK_CARD, "cards updated");
    }
}
