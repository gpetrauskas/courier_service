package gytis.courier.application.service.order;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.order.CancelOrderUseCase;
import gytis.courier.application.port.in.order.PlaceOrderUseCase;
import gytis.courier.application.port.in.payment.CreatePaymentUseCase;
import gytis.courier.application.port.out.delivery.DeliveryOptionCommandPort;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.order.OrderCommandPort;
import gytis.courier.application.service.address.AddressResolver;
import gytis.courier.domain.delivery.DeliveryOption;
import gytis.courier.domain.event.OrderCanceledEvent;
import gytis.courier.domain.order.Order;
import gytis.courier.domain.order.OrderAddress;
import gytis.courier.domain.order.Parcel;
import gytis.courier.domain.order.PlaceOrderCommand;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCommandService implements PlaceOrderUseCase, CancelOrderUseCase {
    private final AddressResolver addressResolver;
    private final DeliveryOptionCommandPort deliveryPort;
    private final OrderCommandPort commandPort;
    private final CreatePaymentUseCase createPaymentUseCase;
    private final DomainEventPublisher publisher;
    private final ActivityLogUseCase logUseCase;

    public OrderCommandService(AddressResolver addressResolver, DeliveryOptionCommandPort deliveryPort, OrderCommandPort commandPort, CreatePaymentUseCase createPaymentUseCase, DomainEventPublisher publisher, ActivityLogUseCase logUseCase) {
        this.addressResolver = addressResolver;
        this.deliveryPort = deliveryPort;
        this.commandPort = commandPort;
        this.createPaymentUseCase = createPaymentUseCase;
        this.publisher = publisher;
        this.logUseCase = logUseCase;
    }

    @Override
    @Transactional
    public Long placeOrder(PlaceOrderCommand command) {
        OrderAddress sender = addressResolver.resolve(command.senderId(), command.sender(), command.userId());
        OrderAddress recipient = addressResolver.resolve(command.recipientId(), command.recipient(), command.userId());

        DeliveryOption deliveryPreference = deliveryPort.findById(command.preferenceId());
        DeliveryOption weight = deliveryPort.findById(command.weightId());
        DeliveryOption size = deliveryPort.findById(command.dimensionsId());

        Parcel parcel = new Parcel(weight, size, command.parcelContents());

        Order order = Order.create(command.userId(), sender, recipient, parcel, deliveryPreference);
        order = commandPort.insert(order);

        createPaymentUseCase.create(order.getId(), order.calculateShippingCost());

        logUseCase.saveLog("USER", "order created", "Order #" + order.getId() + " - " + order.getDeliveryMethodName() + " delivery, " +
                order.getParcel().getWeightDisplayName() + " weight, " + order.getParcel().getDimensionsDisplayName() + " size");

        return order.getId();
    }

    @Override
    public void cancel(Long id, Long userId) {
        Order order = commandPort.getForUser(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderCanceledEvent canceledEvent = order.cancel();
        commandPort.save(order);

        publisher.publish(canceledEvent);

        logUseCase.saveLog("USER", "order canceled", "Order #" + order.getId() + " was canceled");
    }
}
