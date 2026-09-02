package gytis.courier.adapter.out.jwt;

import gytis.courier.application.port.out.auth.RefreshTokenResult;
import gytis.courier.application.port.out.auth.RefreshTokenValidationResult;
import gytis.courier.application.port.out.auth.TokenGeneratorPort;
import gytis.courier.application.port.out.auth.TokenValidatorPort;
import org.springframework.stereotype.Component;

@Component
public class JwtAdapter implements TokenGeneratorPort, TokenValidatorPort {
    private final JwtService jwtService;

    public JwtAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(Long id, String email, String role, String name) {
        return jwtService.createToken(id, email, role, name);
    }

    @Override
    public RefreshTokenResult generateRefreshToken(Long id) {
        return jwtService.createRefreshToken(id);
    }

    @Override
    public RefreshTokenValidationResult validateRefreshToken(String token) {
        return jwtService.validateRefreshToken(token);
    }
}
