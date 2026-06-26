package gytis.courier.application.port.in.delivery;

import gytis.courier.application.service.delivery.CreateDeliveryOptionCommand;

public interface AddDeliveryOptionUseCase {
    void add(CreateDeliveryOptionCommand command);
}
