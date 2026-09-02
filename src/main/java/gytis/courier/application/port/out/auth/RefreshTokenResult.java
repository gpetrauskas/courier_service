package gytis.courier.application.port.out.auth;

import java.time.LocalDateTime;

public record RefreshTokenResult(String token, String jti, LocalDateTime createdAt, LocalDateTime expiresAt) {
}
