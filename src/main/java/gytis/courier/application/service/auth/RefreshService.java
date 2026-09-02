package gytis.courier.application.service.auth;

import gytis.courier.application.port.in.auth.AuthTokens;
import gytis.courier.application.port.in.auth.RefreshUseCase;
import gytis.courier.application.port.out.auth.RefreshTokenCommandPort;
import gytis.courier.application.port.out.auth.RefreshTokenValidationResult;
import gytis.courier.application.port.out.auth.TokenValidatorPort;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.domain.person.Person;
import gytis.courier.domain.refresh.RefreshToken;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshService implements RefreshUseCase {
    private final TokenValidatorPort validatorPort;
    private final PersonCommandPort personCommandPort;
    private final RefreshTokenCommandPort refreshPort;
    private final AuthTokenIssuer tokenIssuer;

    public RefreshService(TokenValidatorPort validatorPort, PersonCommandPort personCommandPort, RefreshTokenCommandPort refreshPort, AuthTokenIssuer tokenIssuer) {
        this.validatorPort = validatorPort;
        this.personCommandPort = personCommandPort;
        this.refreshPort = refreshPort;
        this.tokenIssuer = tokenIssuer;
    }

    @Transactional
    @Override
    public AuthTokens refresh(String token) {
        System.out.println("as cia 2");
        RefreshTokenValidationResult result = validatorPort.validateRefreshToken(token);
        Person person = personCommandPort.findById(result.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (person.isBlocked()) {
            throw new IllegalArgumentException("User is banned");
        }

        RefreshToken refreshToken = refreshPort.findByJti(result.jti()).
                orElseThrow(() -> new BadCredentialsException("Invalid token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BadCredentialsException("Invalid token");
        }

        if (refreshToken.isUsed()) {
            refreshPort.revokeAll(result.personId());
            throw new BadCredentialsException("Invalid token - reuse detected");
        }

        refreshPort.markUsed(result.jti());
        return tokenIssuer.issue(person.getId(), person.getEmail().email(), person.getRole(), person.getName());
    }
}
