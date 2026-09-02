package gytis.courier.application.service.auth;

import gytis.courier.application.port.in.auth.AuthTokens;
import gytis.courier.application.port.out.auth.RefreshTokenCommandPort;
import gytis.courier.application.port.out.auth.RefreshTokenResult;
import gytis.courier.application.port.out.auth.TokenGeneratorPort;
import gytis.courier.domain.refresh.RefreshToken;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenIssuer {
    private final TokenGeneratorPort port;
    private final RefreshTokenCommandPort commandPort;

    public AuthTokenIssuer(TokenGeneratorPort port, RefreshTokenCommandPort commandPort) {
        this.port = port;
        this.commandPort = commandPort;
    }

    public AuthTokens issue(Long id, String email, String role, String name) {
        String access = port.generateToken(id, email, role, name);
        RefreshTokenResult refresh = port.generateRefreshToken(id);

        createAndSaveRefreshToken(refresh, id);

        return new AuthTokens(access, refresh.token());
    }

    private void createAndSaveRefreshToken(RefreshTokenResult result, Long personid) {
        RefreshToken refreshToken = RefreshToken.create(result.jti(), personid, result.createdAt(), result.expiresAt());

        commandPort.save(refreshToken);
    }
}
