package gytis.courier.application.service.regstration;

import gytis.courier.application.port.in.registration.RegisterUseCase;
import gytis.courier.application.port.in.registration.RegistrationCommand;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.application.port.out.auth.PasswordHashingPort;
import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.domain.person.User;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService implements RegisterUseCase {
    private final PasswordHashingPort passwordHashingPort;
    private final UserCommandPort userCommandPort;
    private final PersonQueryPort personQueryPort;

    public RegistrationService(PasswordHashingPort passwordHashingPort, UserCommandPort userCommandPort, PersonQueryPort personQueryPort) {
        this.passwordHashingPort = passwordHashingPort;
        this.userCommandPort = userCommandPort;
        this.personQueryPort = personQueryPort;
    }

    @Override
    public void register(RegistrationCommand command) {
        if (personQueryPort.existsByEmail(command.email().email())) {
            throw new IllegalStateException("Email is already used");
        }

        User user = new User(null, command.name(), command.email(), passwordHashingPort.encode(command.password().password()));
        userCommandPort.create(user);
    }
}
