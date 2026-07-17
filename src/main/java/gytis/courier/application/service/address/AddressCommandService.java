package gytis.courier.application.service.address;

import gytis.courier.application.command.PartialAddressUpdateCommand;
import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.address.DeleteAddressUseCase;
import gytis.courier.application.port.in.address.UpdateAddressUseCase;
import gytis.courier.application.port.out.address.AddressPersistencePort;
import gytis.courier.domain.address.Address;
import gytis.courier.exception.AddressNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressCommandService implements UpdateAddressUseCase, DeleteAddressUseCase {
    private static final Logger logger = LoggerFactory.getLogger(AddressCommandService.class);
    private final AddressPersistencePort port;
    private final ActivityLogUseCase logUseCase;

    public AddressCommandService(AddressPersistencePort port, ActivityLogUseCase logUseCase) {
        this.port = port;
        this.logUseCase = logUseCase;
    }

    @Override
    @Transactional
    public void updateAddress(PartialAddressUpdateCommand command, Long addressId, Long userId) {
        Address address = findAddressForCurrentPerson(addressId, userId);
        address.partialUpdate(command);

        port.save(address);
        logger.info("updated address with id: {}", addressId);
        logUseCase.saveLog("USER", "address update", "Address #" + address.getId() + " was updated");
    }

    @Override
    @Transactional
    public void deleteAddressById(Long addressId, Long userId) {
        findAddressForCurrentPerson(addressId, userId);
        port.deleteById(addressId);
        logger.info("Deleted address with id: {}", addressId);
        logUseCase.saveLog("USER", "address delete", "Address #" + addressId + " was deleted");

    }

    /* Helper methods
     */
    private Address findAddressForCurrentPerson(Long addressId, Long userId) {
        return port.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Address was not found using userId: "
                        + userId + " and addressId: " + addressId));
    }
}
