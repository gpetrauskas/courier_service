package gytis.courier.application.service.activitylog;

import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.out.activitylog.ActivityLogPort;
import gytis.courier.application.port.out.websocket.WebSocketPublisherPort;
import gytis.courier.common.SecurityUtils;
import gytis.courier.domain.activitylog.ActivityCategory;
import gytis.courier.domain.activitylog.ActivityLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogService implements ActivityLogUseCase {
    private final ActivityLogPort port;
    private final WebSocketPublisherPort wsPort;

    public ActivityLogService(ActivityLogPort port, WebSocketPublisherPort wsPort) {
        this.port = port;
        this.wsPort = wsPort;
    }

    @Override
    public void saveLog(String role, ActivityCategory category, String action, String description) {
        String userEmail = SecurityUtils.getCurrentPersonEmail();

        port.save(new ActivityLog(userEmail, role, category, action, description));
        publishToWs(category);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String user, String role, ActivityCategory category, String action, String description) {
        port.save(new ActivityLog(user, role, category, action, description));
        publishToWs(category);
    }

    private void publishToWs(ActivityCategory category) {
        wsPort.broadcast(WebSocketDestinations.ACTIVITY + "/" + category, "activity updated");
    }
}
