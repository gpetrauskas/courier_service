package gytis.courier.application.port.in.order;

import gytis.courier.domain.order.PlaceOrderCommand;

public interface PlaceOrderUseCase {
    Long placeOrder(PlaceOrderCommand command);
}
