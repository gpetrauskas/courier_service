package gytis.courier.application.service.auth;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.auth.LoginCommand;
import gytis.courier.application.port.in.auth.AuthTokens;
import gytis.courier.application.port.in.auth.LoginUseCase;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.application.port.out.auth.PasswordHashingPort;
import gytis.courier.domain.person.Person;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService implements LoginUseCase {
    private final PasswordHashingPort passwordHashingPort;
    private final PersonCommandPort personCommandPort;
    private final ActivityLogUseCase logUseCase;
    private final AuthTokenIssuer authTokenIssuer;

    public LoginService(PasswordHashingPort passwordHashingPort, PersonCommandPort personCommandPort, ActivityLogUseCase logUseCase, AuthTokenIssuer authTokenIssuer) {
        this.passwordHashingPort = passwordHashingPort;
        this.personCommandPort = personCommandPort;
        this.logUseCase = logUseCase;
        this.authTokenIssuer = authTokenIssuer;
    }

    @Override
    @Transactional
    public AuthTokens login(LoginCommand command) {
        Person person = personCommandPort.findByEmail(command.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordHashingPort.matches(command.password(), person.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (person.isBlocked()) {
            throw new IllegalArgumentException("Account is banned");
        }

        AuthTokens result = authTokenIssuer.issue(person.getId(), person.getEmail().email(), person.getRole(), person.getName());

        logUseCase.saveLog(person.getEmail().email(), person.getRole(), "Logged In", "Person logs in");
        return result;
    }
}
