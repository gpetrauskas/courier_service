package gytis.courier.application.port.in.auth;

public interface RefreshUseCase {
    AuthTokens refresh(String token);
}
