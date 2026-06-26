package gytis.courier.application.port.in.auth;

public record LoginCommand(
        String email,
        String password
) {
}
