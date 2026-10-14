package gytis.courier.application.port.in.activityLog;

import gytis.courier.domain.activitylog.ActivityCategory;

public interface ActivityLogUseCase {
    void saveLog(String role, ActivityCategory category, String action, String description);
    void saveLog(String user, String role, ActivityCategory category, String action, String description);
}
