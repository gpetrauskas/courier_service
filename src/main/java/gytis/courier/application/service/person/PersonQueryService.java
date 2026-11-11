package gytis.courier.application.service.person;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.person.PersonQueryUseCase;
import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.application.query.filter.PersonQuery;
import gytis.courier.application.readmodel.person.AdminPersonDetailsReadModel;
import gytis.courier.application.readmodel.person.AdminPersonListReadModel;
import gytis.courier.application.readmodel.person.AdminUserStatsReadModel;
import gytis.courier.application.readmodel.person.MyInfoReadModel;
import gytis.courier.domain.common.StatsPeriod;
import gytis.courier.domain.person.Role;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class PersonQueryService implements PersonQueryUseCase {
    private final PersonQueryPort port;
    private final Clock clock;

    public PersonQueryService(PersonQueryPort port, Clock clock) {
        this.port = port;
        this.clock = clock;
    }

    public MyInfoReadModel getMyInfo(Long myId, String role) {
        return port.getMyInfo(myId, role)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
    }

    public PageResult<AdminPersonListReadModel> getAll(PersonQuery query, PageQuery pageQuery) {
        return port.getAll(query, pageQuery);
    }

    @Override
    public AdminUserStatsReadModel getStats(StatsPeriod period) {
        LocalDateTime current = LocalDateTime.now(clock);
        LocalDateTime from = period.startFrom(current);

        return port.getStats(from);
    }

    public AdminPersonDetailsReadModel getAdminDetailedById(Long id) {
        Role role = port.findPersonRole(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        return switch (role) {
            case Role.ADMIN -> port.getAdminAdminDetailed(id)
                    .orElseThrow();
            case Role.COURIER -> port.getAdminCourierDetailed(id)
                    .orElseThrow();
            case Role.USER -> port.getAdminUserDetailed(id)
                    .orElseThrow();
        };
    }
}
