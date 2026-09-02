package gytis.courier.application.port.out.auth;

public record RefreshTokenValidationResult(Long personId, String jti) {
}
