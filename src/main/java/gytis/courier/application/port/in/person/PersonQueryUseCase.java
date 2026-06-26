package gytis.courier.application.port.in.person;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.PersonQuery;
import gytis.courier.application.readmodel.person.AdminPersonDetailsReadModel;
import gytis.courier.application.readmodel.person.AdminPersonListReadModel;
import gytis.courier.application.readmodel.person.MyInfoReadModel;

public interface PersonQueryUseCase {
    MyInfoReadModel getMyInfo(Long userId, String role);
    AdminPersonDetailsReadModel getAdminDetailedById(Long id);
    PageResult<AdminPersonListReadModel> getAll(PersonQuery personQuery, PageQuery pageQuery);
}
