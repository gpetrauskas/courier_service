package gytis.courier.application.port.in.delivery;

import gytis.courier.application.service.delivery.UpdateDeliveryOptionCommand;

public interface UpdateDeliveryOptionUseCase {
    void updateDeliveryMethod(UpdateDeliveryOptionCommand command);
}
