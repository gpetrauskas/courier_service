/*
package com.example.courier.orderservicetest;

import order.domain.gytis.courier.OrderStatus;
import order.domain.gytis.courier.ParcelStatus;
import payment.domain.gytis.courier.PaymentStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.Payment;
import exception.gytis.courier.OrderCancellationException;
import exception.gytis.courier.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.payment.PaymentService;
import com.example.courier.service.order.command.OrderCommandServiceImpl;
import com.example.courier.service.order.query.OrderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelOrderTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderQueryService queryService;
    @InjectMocks
    OrderCommandServiceImpl commandService;

    @BeforeEach
    void setUp() {
    }

    private Order createTestOrder(OrderStatus status, Parcel parcel) {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setParcelDetails(parcel);
        order.setCreateDate(LocalDateTime.now());

        return order;
    }

    private Payment createTestPayment(PaymentStatus status, Order order) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setStatus(status);

        return payment;
    }

    private Parcel createTestParcel(ParcelStatus status) {
        Parcel parcel = new Parcel();
        parcel.setId(1L);
        parcel.setStatus(status);

        return parcel;
    }

    @Nested
    @DisplayName("success tests")
    class SuccessTests {

        @Test
        @DisplayName("cancel order -  change statuses, should success")
        void cancelOrder_shouldSuccess() {
            Parcel parcel = createTestParcel(ParcelStatus.WAITING_FOR_PAYMENT);
            Order order = createTestOrder(OrderStatus.PENDING, parcel);
            Payment payment = createTestPayment(PaymentStatus.NOT_PAID, order);
            order.setPayment(payment);

            when(queryService.getOrderByIdAndCurrentPersonId(order.getId())).thenReturn(order);

            commandService.cancelOrder(order.getId());

            assertEquals(OrderStatus.CANCELED, order.getStatus());
            assertEquals(ParcelStatus.CANCELED, parcel.getStatus());
            assertEquals(PaymentStatus.CANCELED, payment.getStatus());

            verify(orderRepository, times(1)).save(order);
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("order not found - should throw exception")
        void cancelOrder_orderNotFound() {
            when(queryService.getOrderByIdAndCurrentPersonId(1L)).thenThrow(new ResourceNotFoundException("Order not found or not owned by the user."));

            assertThatThrownBy(() -> commandService.cancelOrder(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Order not found or not owned by the user.");

            verify(paymentService, never()).getPaymentByOrderIdAndUserId(any());
        }

        @Test
        @DisplayName("invalid order status for canceling - should throw")
        void cancelOrder_invalidOrderStatusToCancel() {
            Parcel parcel = createTestParcel(ParcelStatus.WAITING_FOR_PAYMENT);
            Order order = createTestOrder(OrderStatus.CONFIRMED, parcel);

            when(queryService.getOrderByIdAndCurrentPersonId(order.getId())).thenReturn(order);

            assertThatThrownBy(() -> commandService.cancelOrder(order.getId()))
                    .isInstanceOf(OrderCancellationException.class)
                    .hasMessageContaining("Order already canceled or confirmed");

            verify(paymentService, never()).savePayment(any());
        }

        @Test
        @DisplayName("already canceled")
        void cancelOrder_canceledAlready() {
            Parcel parcel = createTestParcel(ParcelStatus.CANCELED);
            Order order = createTestOrder(OrderStatus.CANCELED, parcel);
            Payment payment = createTestPayment(PaymentStatus.CANCELED, order);

            when(queryService.getOrderByIdAndCurrentPersonId(order.getId())).thenReturn(order);

            assertThatThrownBy(() -> commandService.cancelOrder(order.getId()))
                    .isInstanceOf(OrderCancellationException.class)
                    .hasMessage("Order already canceled or confirmed");

            verify(paymentService, never()).savePayment(payment);
        }
    }
}
*/
