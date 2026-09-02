package gytis.courier.adapter.out.persistence.refreshtoken;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshTokenEntity> findByJti(String jti);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.personId = :personId")
    void revokeAllByPersonId(@Param("personId") Long personId);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.used = true WHERE r.revoked = false AND r.used = false AND r.jti = :jti")
    void markAsUsed(@Param("jti") String jti);
}
