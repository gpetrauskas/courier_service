package gytis.courier.application.port.out.auth;

public interface TokenGeneratorPort {
    String generateToken(Long id, String email, String role, String name);
    String generateRefreshToken(Long id);
}
