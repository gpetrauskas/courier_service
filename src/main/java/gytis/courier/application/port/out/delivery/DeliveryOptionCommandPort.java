package gytis.courier.application.port.out.delivery;

import gytis.courier.domain.delivery.DeliveryOption;

import java.util.Optional;

public interface DeliveryOptionCommandPort {
    DeliveryOption findById(Long id);
    Optional<DeliveryOption> findByName(String name);

    void save(DeliveryOption deliveryOption);
    void create(DeliveryOption deliveryOption);
    void delete(Long id);
}
