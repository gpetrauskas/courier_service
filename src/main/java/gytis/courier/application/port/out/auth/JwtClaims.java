package gytis.courier.application.port.out.auth;

public record JwtClaims(
        Long id,
        String subject,
        String role,
        String name
) {
}