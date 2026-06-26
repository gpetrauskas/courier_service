package gytis.courier.adapter.in.rest.auth.dto;

import gytis.courier.application.port.in.auth.LoginCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    LoginCommand toCommand(LoginRequest request);
}
