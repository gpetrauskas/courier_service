package gytis.courier.adapter.out.persistence.activitylog;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.activitylog.ActivityLogPort;
import gytis.courier.application.port.out.activitylog.ActivityLogQueryPort;
import gytis.courier.application.query.filter.ActivityLogQuery;
import gytis.courier.application.readmodel.activitylog.ActivityLogReadModel;
import gytis.courier.domain.activitylog.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogAdapter implements ActivityLogPort, ActivityLogQueryPort {
    private final ActivityLogJpaRepository repository;
    private final ActivityLogMapper mapper;

    public ActivityLogAdapter(ActivityLogJpaRepository repository, ActivityLogMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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

    @Override
    public PageResult<ActivityLogReadModel> getAll(PageQuery pageQuery, ActivityLogQuery logQuery) {
        Specification<ActivityLogJpaEntity> specification = ActivityLogSpecification.from(logQuery);
        Pageable pageable = PageableFactory.from(pageQuery);


        System.out.println("role: " + logQuery.role());
        Page<ActivityLogJpaEntity> page = repository.findAll(specification, pageable);

        return PageResultMapper.from(page, mapper::toReadModel);
    }
}
