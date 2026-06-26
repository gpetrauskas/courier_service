package gytis.courier.application.port.in.order;

import gytis.courier.domain.order.OrderAddressSectionUpdateCommand;
import gytis.courier.domain.order.OrderSectionUpdateCommand;
import gytis.courier.domain.order.ParcelSectionUpdateCommand;

public interface AdminOrderUpdateUseCase {
    void updateOrderSection(Long id, OrderSectionUpdateCommand command);
    void parcelSectionUpdate(Long id, ParcelSectionUpdateCommand command);
    void orderAddressSectionUpdate(Long id, OrderAddressSectionUpdateCommand command);
    void markAsPaid(Long orderId);
}
