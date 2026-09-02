package gytis.courier.application.port.out.auth;

import gytis.courier.domain.refresh.RefreshToken;

import java.util.Optional;

public interface RefreshTokenCommandPort {
    void save(RefreshToken token);
    Optional<RefreshToken> findByJti(String jti);
    void markUsed(String jti);
    void revokeAll(Long personId);
}
