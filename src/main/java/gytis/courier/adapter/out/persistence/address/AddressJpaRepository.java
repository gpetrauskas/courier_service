package gytis.courier.adapter.out.persistence.address;

import gytis.courier.adapter.out.persistence.address.projection.AddressProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressJpaRepository extends JpaRepository<AddressJpaEntity, Long> {
    Optional<AddressJpaEntity> findByIdAndUserId(Long addressId, Long userId);


    @Query("""
        SELECT
            a.id AS id,
            a.details.name AS name,
            a.details.street AS street,
            a.details.houseNumber AS houseNumber,
            a.details.flatNumber AS flatNumber,
            a.details.city AS city,
            a.details.postCode AS postCode,
            a.details.phoneNumber AS phoneNumber
        FROM AddressJpaEntity a
        WHERE a.userId = :userId
        ORDER BY a.id DESC
""")
    List<AddressProjection> findByUserId(Long userId);

    @Query("SELECT (COUNT(*) > 0) FROM AddressJpaEntity a WHERE a.id = :addressId AND a.userId = :userId")
    boolean addressBelongToUser(Long addressId, Long userId);
}
