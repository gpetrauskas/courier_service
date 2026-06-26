package gytis.courier.adapter.out.persistence.notification;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.notification.NotificationCommandPort;
import gytis.courier.application.port.out.notification.NotificationQueryPort;
import gytis.courier.application.query.filter.AdminNotificationQuery;
import gytis.courier.application.readmodel.notification.NotificationReadModel;
import gytis.courier.domain.notification.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NotificationAdapter implements NotificationCommandPort, NotificationQueryPort {
    private final NotificationJpaRepository repository;
    private final NotificationEntityMapper mapper;
    private final NotificationReaModelMapper reaModelMapper;

    public NotificationAdapter(NotificationJpaRepository repository, NotificationEntityMapper mapper, NotificationReaModelMapper reaModelMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.reaModelMapper = reaModelMapper;
    }

    @Override
    public void save(Notification notification) {
        NotificationJpaEntity entity = repository.findById(notification.getId()).orElseThrow();
        mapper.updateEntity(notification, entity);
        repository.save(entity);
    }

    @Override
    public Notification create(Notification notification) {
        NotificationJpaEntity entity = mapper.toEntity(notification);
        NotificationJpaEntity saved = repository.save(entity);

        return notification.withId(saved.getId());
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public PageResult<NotificationReadModel> getAll(PageQuery pageQuery, AdminNotificationQuery query) {
        Pageable pageable = PageableFactory.from(pageQuery);
        Specification<NotificationJpaEntity> specification = NotificationSpecificationBuilder.from(query);
        var resp = repository.findAll(specification, pageable);

        return PageResultMapper.from(resp, reaModelMapper::toReadModel);
    }


}
