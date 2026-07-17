package gytis.courier.application.port.out.activitylog;

import gytis.courier.domain.activitylog.ActivityLog;

public interface ActivityLogPort {
    void save(ActivityLog activityLog);
}
