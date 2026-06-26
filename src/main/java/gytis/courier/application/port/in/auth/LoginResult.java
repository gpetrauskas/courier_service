package gytis.courier.application.port.in.auth;

public record LoginResult(
        String jwt,
        String refresh
) {
}
