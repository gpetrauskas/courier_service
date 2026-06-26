package gytis.courier.adapter.out.jwt;

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
    public String generateRefreshToken(Long id) {
        return jwtService.createRefreshToken(id);
    }

    @Override
    public Long validateRefreshToken(String token) {
        return jwtService.validateRefreshToken(token);
    }
}
