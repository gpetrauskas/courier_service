package gytis.courier.adapter.out.persistence.person.user;

import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import gytis.courier.adapter.out.persistence.person.projection.*;
import gytis.courier.application.readmodel.person.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonInfoReadModelMapper {
    MyUserInfoReadModel toReadModel(UserInfoProjection projection);
    MyCourierInfoReadModel toReadModel(CourierInfoProjection projection);
    MyAdminInfoReadModel toReadModel(AdminInfoProjection projection);

    // admin view
    @Mapping(target = "role", constant = "ADMIN")
    AdminAdminDetailsReadModel toAdminDetailed(AdminViewAdminProjection projection);
    @Mapping(target = "hasActiveTask", source = "activeTask")
    @Mapping(target = "role", constant = "COURIER")
    AdminCourierDetailsReadModel toAdminDetailed(AdminViewCourierProjection projection);
    @Mapping(target = "role", constant = "USER")
    AdminUserDetailsReadModel toAdminDetailed(AdminViewUserProjection projection);

    @Mapping(target = "banned", source = "blocked")
    AdminPersonListReadModel toAdminList(AdminPersonListProjection projection);

    @Mapping(target = "banned", source = "blocked")
    AdminPersonListReadModel toAdminList(PersonJpaEntity entity);

/*    UserReadModel toReadModel(UserProjection projection);*/
    CourierReadModel toReadModel(CourierProjection projection);
}
