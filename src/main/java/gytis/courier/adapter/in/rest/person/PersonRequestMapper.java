package gytis.courier.adapter.in.rest.person;

import gytis.courier.adapter.common.CommonValueObjectMapper;
import gytis.courier.adapter.in.rest.person.dto.PersonDetailsUpdateRequest;
import gytis.courier.adapter.in.rest.person.dto.UserSelfEditRequest;
import gytis.courier.application.command.UpdatePersonCommand;
import gytis.courier.application.command.UserSelfEditCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CommonValueObjectMapper.class })
public interface PersonRequestMapper {
    UpdatePersonCommand toUpdateCommand(PersonDetailsUpdateRequest request);
    UserSelfEditCommand toUserSelfUpdateCommand(UserSelfEditRequest request, Long userId);
}
