package gytis.courier.application.port.in.activityLog;

public interface ActivityLogUseCase {
    void saveLog(String role, String action, String description);
    void saveLog(String user, String role, String action, String description);
}
