package gytis.courier.application.service.order;

import gytis.courier.application.port.in.order.AdminOrderUpdateUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.delivery.DeliveryOptionCommandPort;
import gytis.courier.application.port.out.order.OrderCommandPort;
import gytis.courier.domain.delivery.DeliveryOption;
import gytis.courier.domain.order.Order;
import gytis.courier.domain.order.OrderAddressSectionUpdateCommand;
import gytis.courier.domain.order.OrderSectionUpdateCommand;
import gytis.courier.domain.order.ParcelSectionUpdateCommand;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderUpdateService implements AdminOrderUpdateUseCase {
    private final OrderCommandPort commandPort;
    private final DeliveryOptionCommandPort deliveryPort;
    private final DomainEventPublisher eventPublisher;

    public AdminOrderUpdateService(OrderCommandPort commandPort, DeliveryOptionCommandPort deliveryPort, DomainEventPublisher eventPublisher
    ) {
        this.commandPort = commandPort;
        this.deliveryPort = deliveryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void updateOrderSection(Long id, OrderSectionUpdateCommand command) {
        Order order = commandPort.getBasicById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        System.out.println(command.status() + " " + command.deliveryMethodName());

        if (command.status() != null) {
            order.updateStatus(command.status());
        }

        if (command.deliveryMethodName() != null) {
            DeliveryOption newPreference = deliveryPort.findByName(command.deliveryMethodName())
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery option not found"));

            order.updateDeliveryMethodPreference(newPreference);
        }

        commandPort.save(order);
    }

    @Override
    public void parcelSectionUpdate(Long id, ParcelSectionUpdateCommand command) {
        Order order = commandPort.getWithParcel(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.updateParcelSection(command);
        commandPort.save(order);
    }

    @Override
    public void orderAddressSectionUpdate(Long id, OrderAddressSectionUpdateCommand command) {
        Order order = commandPort.getWithParcelAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        System.out.println("flat " + order.getSenderAddress().getDetails().getFlatNumber());

        System.out.println(command.id() + " " + command.flatNumber() + " " + command.selectedAddress());

        var maybeEvent = order.updateAddress(command);
        maybeEvent.ifPresent(eventPublisher::publish);

        System.out.println("updated flat " + order.getSenderAddress().getDetails().getFlatNumber());

        commandPort.save(order);
    }

    @Override
    public void markAsPaid(Long orderId) {
        Order order = commandPort.getWithParcel(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.markConfirmed();
        commandPort.save(order);
    }
}
