package gytis.courier.application.service.person;

import gytis.courier.application.command.UserSelfEditCommand;
import gytis.courier.application.port.in.paymentmethod.DeletePaymentMethodUseCase;
import gytis.courier.application.port.in.person.EditMyInfoUseCase;
import gytis.courier.application.port.out.address.AddressPersistencePort;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.domain.person.User;
import gytis.courier.exception.ResourceNotFoundException;
import gytis.courier.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserCommandService implements DeletePaymentMethodUseCase, EditMyInfoUseCase {
    private final UserCommandPort userPort;
    private final AddressPersistencePort addressPort;

    public UserCommandService(UserCommandPort userPort, AddressPersistencePort addressPort) {
        this.userPort = userPort;
        this.addressPort = addressPort;
    }

    @Override
    public void editMyInfo(UserSelfEditCommand command) {
        User me = userPort.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        System.out.println("edit method cia: " + command.defaultAddressId());

        if (command.defaultAddressId() != null) {
            if (!addressPort.addressIdOwnedByUserId(command.defaultAddressId(), command.userId())) {
                throw new UnauthorizedAccessException("Invalid address id");
            }
        }

        me.selfUpdate(command);
        userPort.save(me);
    }

    @Override
    public void delete(Long methodId, Long userId) {
        User user = userPort.findWithPaymentMethodsById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.removePaymentMethod(methodId);

        userPort.deletePaymentMethod(methodId, userId);
    }
}
