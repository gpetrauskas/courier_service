package gytis.courier.application.port.out.activitylog;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.ActivityLogQuery;
import gytis.courier.application.readmodel.activitylog.ActivityLogReadModel;

public interface ActivityLogQueryPort {
    PageResult<ActivityLogReadModel> getAll(PageQuery pageQuery, ActivityLogQuery logQuery);
}
