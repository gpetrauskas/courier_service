package gytis.courier.adapter.out.persistence.person.user;

import gytis.courier.adapter.out.persistence.person.projection.AdminViewUserProjection;
import gytis.courier.adapter.out.persistence.person.projection.UserInfoProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    @EntityGraph(attributePaths = "paymentMethods")
    Optional<UserJpaEntity> findWithPaymentMethodsById(Long id);

    @EntityGraph(attributePaths = "addresses")
    Optional<UserJpaEntity> findWithAddressesById(Long id);

    @EntityGraph(attributePaths = "orders")
    Optional<UserJpaEntity> findWithOrdersById(Long id);

    @EntityGraph(attributePaths = "paymentMethods, addresses, orders")
    Optional<UserJpaEntity> findWithAllDataById(Long id);

    @Query("SELECT u.id FROM UserJpaEntity u WHERE u.blocked = false AND u.deleted = false")
    List<Long> findAllActiveIds(Pageable pageable);

    boolean existsByEmail(String email);

    @Query("""
    SELECT u.name AS name,
           u.email AS email,
           u.subscribed AS subscribed,
           u.phoneNumber AS phoneNumber,
           (SELECT COUNT(o) FROM OrderJpaEntity o WHERE o.userId = :id) AS orderCount,
           a.details.street AS defaultAddress
    FROM UserJpaEntity u
    LEFT JOIN AddressJpaEntity a ON a.id = u.defaultAddressId
    WHERE u.id = :id
    """)
    Optional<UserInfoProjection> getUserInfo(Long id);

    @Query("""
    SELECT u.id AS id,
           u.name AS name,
           u.email AS email,
           u.phoneNumber AS phoneNumber,
           u.blocked AS blocked,
           u.deleted AS deleted,
           u.deletedDate AS deletedDate,
           u.subscribed AS subscribed,
           COUNT(o.id) AS orderCount
    FROM UserJpaEntity u
    LEFT JOIN OrderJpaEntity o ON o.userId = u.id
    WHERE u.id = :id
""")
    Optional<AdminViewUserProjection> getAdminUserDetailsById(Long id);
}
