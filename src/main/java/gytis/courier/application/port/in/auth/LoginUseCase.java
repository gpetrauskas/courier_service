package gytis.courier.application.port.in.auth;

public interface LoginUseCase {
    AuthTokens login(LoginCommand command);
}
