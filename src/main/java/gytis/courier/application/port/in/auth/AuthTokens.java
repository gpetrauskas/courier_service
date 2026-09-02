package gytis.courier.application.port.in.auth;

public record AuthTokens(
        String jwt,
        String refresh
) {
}
