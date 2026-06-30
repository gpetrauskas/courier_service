package gytis.courier;

import gytis.courier.application.port.in.payment.CreatePaymentUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.delivery.DeliveryOptionCommandPort;
import gytis.courier.application.port.out.order.OrderCommandPort;
import gytis.courier.application.service.address.AddressResolver;
import gytis.courier.application.service.order.AddressInput;
import gytis.courier.application.service.order.OrderCommandService;
import gytis.courier.domain.address.AddressDetails;
import gytis.courier.domain.delivery.DeliveryOption;
import gytis.courier.domain.event.OrderCanceledEvent;
import gytis.courier.domain.order.*;
import gytis.courier.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderCommandServiceTest {
    private final DeliveryOption size = new DeliveryOption(1L, "medium_size", "max 1mx1mx1m", BigDecimal.valueOf(1), false);
    private final DeliveryOption weight = new DeliveryOption(2L, "heavy_weight", "max 30kg", BigDecimal.valueOf(7), false);
    private final DeliveryOption preference = new DeliveryOption(3L, "standard", "1-3 days delivery", BigDecimal.valueOf(5), false);
    private final OrderAddress orderAddress = new OrderAddress(new AddressDetails(
            "bilbo", "elm street", "13", "1", "nyw york", "12345", "12345678"
    ));
    private final AddressInput addressInput = new AddressInput("bilbo", "elm street", "13", "1", "nyw york", "12345", "12345678");
    private final PlaceOrderCommand command = new PlaceOrderCommand(99L, null, null, addressInput, addressInput, "washing machine", 2L, 1L, 3L);
    private final Parcel parcel = new Parcel(weight, size, "washing machine");


    @Mock
    private AddressResolver addressResolver;
    @Mock
    private DeliveryOptionCommandPort deliveryOptionCommandPort;
    @Mock
    private OrderCommandPort orderCommandPort;
    @Mock
    private CreatePaymentUseCase createPaymentUseCase;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private OrderCommandService orderCommandService;

    @Test
    void successfullyPlaceOrder() {
        Order orderWithId = Order.restore(
                10L, command.userId(), orderAddress, orderAddress, parcel, preference.id(),
                preference.name(), preference.description(), preference.price(), OrderStatus.PENDING, LocalDateTime.now()
        );

        when(addressResolver.resolve(command.senderId(), command.sender(), command.userId())).thenReturn(orderAddress);
        when(addressResolver.resolve(command.recipientId(), command.recipient(), command.userId())).thenReturn(orderAddress);
        when(deliveryOptionCommandPort.findById(1L)).thenReturn(size);
        when(deliveryOptionCommandPort.findById(2L)).thenReturn(weight);
        when(deliveryOptionCommandPort.findById(3L)).thenReturn(preference);
        when(orderCommandPort.insert(any())).thenReturn(orderWithId);

        Long orderId = orderCommandService.placeOrder(command);

        verify(createPaymentUseCase).create(orderId, orderWithId.calculateShippingCost());

        assertNotNull(orderId);
        assertEquals(orderWithId.getId(), orderId);
    }

    @Test
    void throwResourceNotFoundWhenDeliveryOptionNotFound() {
        when(addressResolver.resolve(command.senderId(), command.sender(), command.userId())).thenReturn(orderAddress);
        when(addressResolver.resolve(command.recipientId(), command.recipient(), command.userId())).thenReturn(orderAddress);

        when(deliveryOptionCommandPort.findById(3L)).thenThrow(new ResourceNotFoundException("Delivery method was not found!"));

        assertThrows(ResourceNotFoundException.class, () -> orderCommandService.placeOrder(command));
    }

    @Test
    void successfullyCanceled() {
        Order orderToCancel = Order.restore(
                1L, command.userId(), orderAddress, orderAddress, parcel, preference.id(),
                preference.name(), preference.description(), preference.price(), OrderStatus.PENDING, LocalDateTime.now()
        );
        when(orderCommandPort.getForUser(1L, 99L)).thenReturn(Optional.of(orderToCancel));

        orderCommandService.cancel(orderToCancel.getId(), command.userId());

        verify(orderCommandPort).save(orderToCancel);
        verify(eventPublisher).publish(any(OrderCanceledEvent.class));
    }

    @Test
    void throwsOnOrderNotFound() {
        when(orderCommandPort.getForUser(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderCommandService.cancel(1L, 2L));
        verify(orderCommandPort, never()).save(any());
        verify(eventPublisher, never()).publish(any(OrderCanceledEvent.class));
    }
}
