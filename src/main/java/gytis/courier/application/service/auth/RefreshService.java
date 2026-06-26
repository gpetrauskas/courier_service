package gytis.courier.application.service.auth;

import gytis.courier.application.port.in.auth.RefreshUseCase;
import gytis.courier.application.port.out.auth.TokenGeneratorPort;
import gytis.courier.application.port.out.auth.TokenValidatorPort;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.domain.person.Person;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RefreshService implements RefreshUseCase {
    private final TokenValidatorPort validatorPort;
    private final PersonCommandPort personCommandPort;
    private final TokenGeneratorPort generateToken;

    public RefreshService(TokenValidatorPort validatorPort, PersonCommandPort personCommandPort, TokenGeneratorPort generateToken) {
        this.validatorPort = validatorPort;
        this.personCommandPort = personCommandPort;
        this.generateToken = generateToken;
    }

    @Override
    public String refresh(String token) {
        Long id = validatorPort.validateRefreshToken(token);
        Person person = personCommandPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (person.isBlocked()) {
            throw new IllegalArgumentException("User is banned");
        }
        return generateToken.generateToken(
                person.getId(),
                person.getEmail().email(),
                person.getRole(),
                person.getName()
        );
    }
}
