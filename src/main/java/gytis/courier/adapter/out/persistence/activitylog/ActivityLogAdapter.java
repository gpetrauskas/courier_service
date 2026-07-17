package gytis.courier.adapter.out.persistence.activitylog;

import gytis.courier.application.port.out.activitylog.ActivityLogPort;
import gytis.courier.domain.activitylog.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogAdapter implements ActivityLogPort {
    private final ActivityLogJpaRepository repository;

    public ActivityLogAdapter(ActivityLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(ActivityLog activityLog) {
        ActivityLogJpaEntity entity = new ActivityLogJpaEntity();
        entity.setUserEmail(activityLog.getEmail());
        entity.setRole(activityLog.getRole());
        entity.setAction(activityLog.getAction());
        entity.setDescription(activityLog.getDescription());
        entity.setCreatedAt(activityLog.getCreatedAt());

        repository.save(entity);
    }
}
