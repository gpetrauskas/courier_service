package gytis.courier.adapter.in.rest.address;

import gytis.courier.adapter.in.rest.address.dto.AddressRequest;
import gytis.courier.application.command.PartialAddressUpdateCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressRequestMapper {
    PartialAddressUpdateCommand toUpdateCommand(AddressRequest request);
}
