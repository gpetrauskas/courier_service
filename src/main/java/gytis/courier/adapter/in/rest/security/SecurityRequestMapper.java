package gytis.courier.adapter.in.rest.security;

import gytis.courier.adapter.in.rest.security.dto.PasswordChangeRequest;
import gytis.courier.application.command.PasswordChangeCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SecurityRequestMapper {
    PasswordChangeCommand toChangePasswordCommand(PasswordChangeRequest request, Long personId);

}
