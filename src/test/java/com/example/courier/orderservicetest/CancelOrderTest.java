package com.example.courier.orderservicetest;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.Payment;
import com.example.courier.exception.OrderCancellationException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.service.order.OrderService;
import com.example.courier.payment.PaymentService;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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
    private PaymentRepository paymentRepository;
    @Mock
    private CurrentPersonService currentPersonService;
    @InjectMocks
    OrderService orderService;

    private final Long testUserId = 123L;

    @BeforeEach
    void setUp() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(testUserId);
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

            when(orderRepository.findByIdAndUserId(order.getId(), testUserId)).thenReturn(Optional.of(order));
            when(paymentService.getPaymentByOrderId(order.getId())).thenReturn(payment);

            orderService.cancelOrder(order.getId());

            assertEquals(OrderStatus.CANCELED, order.getStatus());
            assertEquals(ParcelStatus.CANCELED, parcel.getStatus());
            assertEquals(PaymentStatus.CANCELED, payment.getStatus());

            verify(paymentService, times(1)).savePayment(payment);
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("order not found - should throw exception")
        void cancelOrder_orderNotFound() {
            when(orderRepository.findByIdAndUserId(1L, testUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.cancelOrder(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Order not found or not owned by the user.");

            verify(paymentService, never()).getPaymentByOrderId(any());
        }

        @Test
        @DisplayName("payment not found - should throw exception")
        void cancelOrder_paymentNotFound() {
            Parcel parcel = createTestParcel(ParcelStatus.WAITING_FOR_PAYMENT);
            Order order = createTestOrder(OrderStatus.PENDING, parcel);

            when(orderRepository.findByIdAndUserId(order.getId(), testUserId)).thenReturn(Optional.of(order));
            when(paymentService.getPaymentByOrderId(order.getId())).thenThrow(new ResourceNotFoundException("Payment not found"));

            assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Payment not found");

            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("invalid order status for canceling - should throw")
        void cancelOrder_invalidOrderStatusToCancel() {
            Parcel parcel = createTestParcel(ParcelStatus.WAITING_FOR_PAYMENT);
            Order order = createTestOrder(OrderStatus.CONFIRMED, parcel);
            Payment payment = createTestPayment(PaymentStatus.PAID, order);


            when(orderRepository.findByIdAndUserId(order.getId(), testUserId)).thenReturn(Optional.of(order));
            when(paymentService.getPaymentByOrderId(order.getId())).thenReturn(payment);

            assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                    .isInstanceOf(OrderCancellationException.class)
                    .hasMessageContaining("Order already confirmed and paid");

            verify(paymentService, never()).savePayment(any());
        }

        @Test
        @DisplayName("already canceled")
        void cancelOrder_canceledAlready() {
            Parcel parcel = createTestParcel(ParcelStatus.CANCELED);
            Order order = createTestOrder(OrderStatus.CANCELED, parcel);
            Payment payment = createTestPayment(PaymentStatus.CANCELED, order);

            when(orderRepository.findByIdAndUserId(order.getId(), testUserId)).thenReturn(Optional.of(order));
            when(paymentService.getPaymentByOrderId(order.getId())).thenReturn(payment);

            assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                    .isInstanceOf(OrderCancellationException.class)
                    .hasMessage("Order already canceled.");

            verify(paymentService, never()).savePayment(payment);
        }
    }
}
