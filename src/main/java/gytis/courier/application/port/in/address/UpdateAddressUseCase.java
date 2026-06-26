package gytis.courier.application.port.in.address;

import gytis.courier.application.command.PartialAddressUpdateCommand;

public interface UpdateAddressUseCase {
    void updateAddress(PartialAddressUpdateCommand request, Long id, Long userId);

}
