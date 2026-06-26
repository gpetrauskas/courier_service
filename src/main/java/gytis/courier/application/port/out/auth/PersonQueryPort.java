package gytis.courier.application.port.out.auth;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.PersonQuery;
import gytis.courier.application.readmodel.person.*;
import gytis.courier.domain.person.Role;

import java.util.List;
import java.util.Optional;

public interface PersonQueryPort {
    Optional<MyInfoReadModel> getMyInfo(Long id, String role);
    PageResult<AdminPersonListReadModel> getAll(PersonQuery query, PageQuery pageQuery);
    Optional<Role> findPersonRole(Long id);


    // admin detailed
    Optional<AdminUserDetailsReadModel> getAdminUserDetailed(Long id);
    Optional<AdminCourierDetailsReadModel> getAdminCourierDetailed(Long id);
    Optional<AdminAdminDetailsReadModel> getAdminAdminDetailed(Long id);

    List<Long> getAllActiveUserIds(int page, int size);
    List<Long> getAllActiveCourierIds(int page, int size);
    List<Long> getAllActiveAdminIds(int page, int size);

    boolean existsByEmail(String email);
}
