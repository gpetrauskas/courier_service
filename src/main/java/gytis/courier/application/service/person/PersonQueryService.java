package gytis.courier.application.service.person;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.person.PersonQueryUseCase;
import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.application.query.filter.PersonQuery;
import gytis.courier.application.readmodel.person.AdminPersonDetailsReadModel;
import gytis.courier.application.readmodel.person.AdminPersonListReadModel;
import gytis.courier.application.readmodel.person.MyInfoReadModel;
import gytis.courier.domain.person.Role;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonQueryService implements PersonQueryUseCase {
    private final PersonQueryPort port;

    public PersonQueryService(PersonQueryPort port) {
        this.port = port;
    }

    public MyInfoReadModel getMyInfo(Long myId, String role) {
        return port.getMyInfo(myId, role)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
    }

    public PageResult<AdminPersonListReadModel> getAll(PersonQuery query, PageQuery pageQuery) {
        return port.getAll(query, pageQuery);
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
