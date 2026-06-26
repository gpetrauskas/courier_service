package gytis.courier.application.port.in.delivery;

import gytis.courier.application.readmodel.deliveryoption.DeliveryOptionReadModel;
import gytis.courier.domain.delivery.DeliveryGroup;

import java.util.List;
import java.util.Map;

public interface DeliveryOptionQueryUseCase {
    DeliveryOptionReadModel getById(Long orderId);
    Map<DeliveryGroup, List<DeliveryOptionReadModel>> getAllCategorized();
    List<DeliveryOptionReadModel> getAllNotCategorized();
}
