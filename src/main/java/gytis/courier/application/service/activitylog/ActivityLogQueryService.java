package gytis.courier.application.service.activitylog;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.activityLog.ActivityLogQueryUseCase;
import gytis.courier.application.port.out.activitylog.ActivityLogQueryPort;
import gytis.courier.application.query.filter.ActivityLogQuery;
import gytis.courier.application.readmodel.activitylog.ActivityLogReadModel;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogQueryService implements ActivityLogQueryUseCase {
    private final ActivityLogQueryPort port;

    public ActivityLogQueryService(ActivityLogQueryPort port) {
        this.port = port;
    }

    @Override
    public PageResult<ActivityLogReadModel> getAll(PageQuery pageQuery, ActivityLogQuery logQuery) {
        return port.getAll(pageQuery, logQuery);
    }
}
