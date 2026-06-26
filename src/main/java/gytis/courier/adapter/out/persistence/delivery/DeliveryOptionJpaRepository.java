package gytis.courier.adapter.out.persistence.delivery;

import gytis.courier.adapter.out.persistence.delivery.projection.DeliveryOptionProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryOptionJpaRepository extends JpaRepository<DeliveryOptionJpaEntity, Long> {
    List<DeliveryOptionProjection> findAllBy();
    Optional<DeliveryOptionJpaEntity> findByName(String name);
    Optional<DeliveryOptionProjection> findProjectedById(Long id);
    List<DeliveryOptionProjection> findAllByDisabledFalse();
}
