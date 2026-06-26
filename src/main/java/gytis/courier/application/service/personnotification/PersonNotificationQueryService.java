package gytis.courier.application.service.personnotification;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageQueryDirection;
import gytis.courier.application.common.PersonNotificationPageResult;
import gytis.courier.application.port.in.personnotification.PersonNotificationQueryUseCase;
import gytis.courier.application.port.out.personnotification.PersonNotificationQueryPort;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonNotificationQueryService implements PersonNotificationQueryUseCase {
    private final PersonNotificationQueryPort queryPort;

    public PersonNotificationQueryService(PersonNotificationQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public PersonNotificationPageResult getAll(PageQuery pageQuery, Long personId) {
        return queryPort.getAll(personId, pageQuery);
    }

    @Override
    public PersonNotificationPageResult getPageContainingNotification(Long nId, Long personId, int pageSize) {
        int index = queryPort.findIndex(personId, nId)
                .orElseThrow(() -> new ResourceNotFoundException("Nothing found"));

        int pageNumber = index / pageSize;

        PageQuery pageQuery = new PageQuery(pageNumber, pageSize, "receivedAt", PageQueryDirection.DESC);
        return queryPort.getAll(personId, pageQuery);
    }
}
