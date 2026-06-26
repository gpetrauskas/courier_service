package gytis.courier.adapter.in.rest.registration;

import gytis.courier.adapter.common.CommonValueObjectMapper;
import gytis.courier.adapter.in.rest.registration.dto.RegistrationRequest;
import gytis.courier.application.port.in.registration.RegistrationCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CommonValueObjectMapper.class })
public interface RegistrationMapper {
    RegistrationCommand toCommand(RegistrationRequest command);
}
