package gytis.courier.application.port.in.auth;

public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}
