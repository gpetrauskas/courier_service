package gytis.courier.adapter.out.persistence.payment;

import gytis.courier.adapter.out.persistence.order.projection.PaymentProjection;
import gytis.courier.adapter.out.persistence.payment.projection.PaymentUserProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentJpaEntity p LEFT JOIN FETCH p.attempts WHERE p.orderId = :orderId")
    Optional<PaymentJpaEntity> findByOrderIdForUpdate(@Param("orderId") Long orderId);
    @Query("SELECT p FROM PaymentJpaEntity p LEFT JOIN FETCH p.attempts WHERE p.orderId = :orderId")
    Optional<PaymentJpaEntity> findByOrderId(@Param("orderId") Long orderId);
    Optional<PaymentUserProjection> findUserProjectionByOrderId(Long id);
    PaymentProjection findAdminProjectionByOrderId(Long id);
}
