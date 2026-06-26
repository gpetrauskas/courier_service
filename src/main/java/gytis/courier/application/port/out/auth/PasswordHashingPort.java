package gytis.courier.application.port.out.auth;

public interface PasswordHashingPort {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
