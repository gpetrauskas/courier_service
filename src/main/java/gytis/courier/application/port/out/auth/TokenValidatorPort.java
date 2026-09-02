package gytis.courier.application.port.out.auth;

public interface TokenValidatorPort {
    RefreshTokenValidationResult validateRefreshToken(String token);
}
