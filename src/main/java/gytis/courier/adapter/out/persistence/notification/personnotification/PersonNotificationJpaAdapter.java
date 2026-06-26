package gytis.courier.adapter.out.persistence.notification.personnotification;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.adapter.out.persistence.notification.NotificationReaModelMapper;
import gytis.courier.application.common.PersonNotificationPageResult;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.port.out.personnotification.PersonNotificationCommandPort;
import gytis.courier.application.port.out.personnotification.PersonNotificationQueryPort;
import gytis.courier.domain.personnotification.PersonNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class PersonNotificationJpaAdapter implements PersonNotificationCommandPort, PersonNotificationQueryPort {
    private final PersonNotificationJpaRepository repository;
    private final PersonNotificationEntityMapper entityMapper;
    private final NotificationReaModelMapper reaModelMapper;

    public PersonNotificationJpaAdapter(PersonNotificationJpaRepository repository, PersonNotificationEntityMapper entityMapper, NotificationReaModelMapper reaModelMapper) {
        this.repository = repository;
        this.entityMapper = entityMapper;
        this.reaModelMapper = reaModelMapper;
    }

    @Override
    public void deliverToRecipients(Long notificationId, List<Long> ids) {
        if (ids.size() < 500) {
            List<PersonNotificationJpaEntity> list = ids.stream()
                    .map(pId -> new PersonNotificationJpaEntity(pId, notificationId))
                    .toList();
            repository.saveAll(list);
        } else {
            repository.bulkDeliverToRecipients(notificationId, ids);
        }
    }

    @Override
    public PersonNotificationPageResult getAll(Long personId, PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);
        var p = repository.findAllByPersonId(personId, pageable);
        long unreadCount = repository.findUnreadCount(personId);



        return new PersonNotificationPageResult(
                PageResultMapper.from(p, reaModelMapper::toPersonReadModel),
                unreadCount);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long personId) {
        repository.markAllAsRead(personId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long personId) {
        repository.markAsRead(id, personId);
    }

    @Override
    public PersonNotification findByPersonIdAndNotificationId(Long personId, Long notificationId) {
        return entityMapper.toDomain(repository.findByIdPersonIdAndIdNotificationId(personId, notificationId));
    }

    @Override
    @Transactional
    public void save(PersonNotification notification) {
        PersonNotificationJpaEntity managed = repository.findByIdPersonIdAndIdNotificationId(
                notification.getId().personId(),
                notification.getId().notificationId()
        );

        entityMapper.updateEntity(notification, managed);
    }

    @Override
    @Transactional
    public void ownDelete(Long myId, Long notificationId) {
        repository.deleteByIdPersonIdAndIdNotificationId(myId, notificationId);
    }

    @Override
    @Transactional
    public void deleteAll(Long myId) {
        repository.deleteAllByIdPersonId(myId);
    }

    @Override
    public Optional<Integer> findIndex(Long myId, Long notificationId) {
        return repository.findNotificationPosition(myId, notificationId);
    }

    @Override
    public int deleteAsAdmin(Long id) {
        return repository.deleteByIdNotificationId(id);
    }
}