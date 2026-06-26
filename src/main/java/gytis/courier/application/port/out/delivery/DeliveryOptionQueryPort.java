package gytis.courier.application.port.out.delivery;

import gytis.courier.application.readmodel.deliveryoption.DeliveryOptionReadModel;

import java.util.List;
import java.util.Optional;

public interface DeliveryOptionQueryPort {
    Optional<DeliveryOptionReadModel> findByIdReadModel(Long id);
    List<DeliveryOptionReadModel> findEnabled();
    List<DeliveryOptionReadModel> findAll();
}
