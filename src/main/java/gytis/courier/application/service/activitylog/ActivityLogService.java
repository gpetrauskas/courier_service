package gytis.courier.application.service.activitylog;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.out.activitylog.ActivityLogPort;
import gytis.courier.common.SecurityUtils;
import gytis.courier.domain.activitylog.ActivityLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogService implements ActivityLogUseCase {
    private final ActivityLogPort port;

    public ActivityLogService(ActivityLogPort port) {
        this.port = port;
    }

    @Override
    public void saveLog(String role, String action, String description) {
        String userEmail = SecurityUtils.getCurrentPersonEmail();

        port.save(new ActivityLog(userEmail, role, action, description));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String user, String role, String action, String description) {
        port.save(new ActivityLog(user, role, action, description));
    }


}
