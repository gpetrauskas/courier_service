package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.adapter.out.persistence.paymentmethod.projection.PaymentMethodProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodJpaRepository extends JpaRepository<PaymentMethodJpaEntity, Long> {
/*
    @Modifying
    @Query("DELETE FROM PaymentMethodJpaEntity p WHERE p.id = :id AND p.userId = :userId")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
*/

    @Modifying
    @Query("DELETE FROM PaymentMethodJpaEntity p WHERE p.id = :methodId AND p.userId = :userId")
    void deleteByIdAndUserId(@Param("methodId") Long methodId, @Param("userId") Long userId);

    @Query("SELECT p.id AS id, p.paymentType AS paymentType, p.saved AS saved, " +
            "CASE TYPE(p) WHEN CreditCardJpaEntity THEN TREAT (p AS CreditCardJpaEntity).last4 ELSE null END AS last4, " +
            "CASE TYPE(p) WHEN PaypalJpaEntity THEN TREAT (p AS PaypalJpaEntity).ppEmail ELSE null END AS ppEmail " +
            "FROM PaymentMethodJpaEntity p WHERE p.userId = :userId and p.saved = true")
    List<PaymentMethodProjection> findAllByUserIdAndSavedTrue(@Param("userId") Long userId);

    @Query("SELECT p.id AS id, p.paymentType AS paymentType, p.saved AS saved, " +
            "CASE TYPE(p) WHEN CreditCardJpaEntity THEN TREAT (p AS CreditCardJpaEntity).last4 ELSE null END AS last4, " +
            "CASE TYPE(p) WHEN PaypalJpaEntity THEN TREAT (p AS PaypalJpaEntity).ppEmail ELSE null END AS ppEmail " +
            "FROM PaymentMethodJpaEntity p WHERE p.id = :id AND p.userId = :userId and p.saved = true")
    Optional<PaymentMethodProjection> findProjectedByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
