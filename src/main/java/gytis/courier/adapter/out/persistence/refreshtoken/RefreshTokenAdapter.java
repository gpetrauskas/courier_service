package gytis.courier.adapter.out.persistence.refreshtoken;

import gytis.courier.application.port.out.auth.RefreshTokenCommandPort;
import gytis.courier.domain.refresh.RefreshToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenAdapter implements RefreshTokenCommandPort {
    private final RefreshTokenJpaRepository repository;
    private final RefreshTokenMapper mapper;

    public RefreshTokenAdapter(RefreshTokenJpaRepository repository, RefreshTokenMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(RefreshToken token) {
        repository.save(mapper.toEntity(token));
    }

    @Override
    public Optional<RefreshToken> findByJti(String jti) {
        return repository.findByJti(jti)
                .map(mapper::toDomain);
    }

    @Override
    public void markUsed(String jti) {
        repository.markAsUsed(jti);
    }

    @Override
    public void revokeAll(Long personId) {
        repository.revokeAllByPersonId(personId);
    }
}
