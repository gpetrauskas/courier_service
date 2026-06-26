package gytis.courier.application.port.out.auth;

public interface TokenValidatorPort {
    Long validateRefreshToken(String token);
}
