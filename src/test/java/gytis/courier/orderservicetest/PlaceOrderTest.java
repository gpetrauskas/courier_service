/*
package com.example.courier.orderservicetest;

import order.domain.gytis.courier.OrderStatus;
import order.domain.gytis.courier.ParcelStatus;
import payment.domain.gytis.courier.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import exception.gytis.courier.PaymentCreationException;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.command.OrderCommandServiceImpl;
import com.example.courier.service.order.factory.OrderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceOrderTest {
    @Mock private OrderFactory orderFactory;
    @Mock private PaymentService paymentService;
    @Mock private OrderRepository repository;

    @InjectMocks private OrderCommandServiceImpl commandService;

    private Order mockOrder() {
        Order order = new Order();
        order.setId(99L);
        order.setParcelDetails(mockParcel());
        return order;
    }

    private Parcel mockParcel() {
        Parcel parcel = new Parcel();
        parcel.setId(3L);
        return parcel;
    }

    private Payment mockPayment() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(20));
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setId(1L);
        return payment;
    }

    private OrderDTO createValidOrderDTO() {
        return new OrderDTO(
                1L,
                new AddressDTO(1L, "city", "streetsend", "123456789", "321", "370111", "12345", "name sender"),
                new AddressDTO(2L, "city", "streerecip", "987654321", "123", "370000", "54321", "name recipient"),
                new ParcelDTO(1L, "5", "1", "contents", null, ParcelStatus.WAITING_FOR_PAYMENT),
                "1",
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @Test
        @DisplayName("Should successfully place order")
        void shouldReturnExpectedMapAndSaveOrderWithCreatedPayment() {
            OrderDTO dto = createValidOrderDTO();
            Order order = mockOrder();

            when(orderFactory.createNewOrderFromDTO(dto)).thenReturn(order);
            doAnswer(inv -> {
                Order o = inv.getArgument(0);
                o.setPayment(mockPayment());
                return null;
            }).when(paymentService).createPayment(any(Order.class));

            var response = commandService.placeOrder(dto);

            assertEquals("Order was placed successfully. Order cost: 20", response.get("message"));
            assertEquals("20", response.get("cost"));
            assertEquals("99", response.get("orderId"));

            assertNotNull(order.getPayment());
            assertEquals(BigDecimal.valueOf(20), order.getPayment().getAmount());
            assertEquals(PaymentStatus.NOT_PAID, order.getPayment().getStatus());

            verify(repository).save(order);
            verify(orderFactory).createNewOrderFromDTO(dto);
            verify(paymentService).createPayment(order);
        }
    }

    @Nested
    @DisplayName("early failure tests")
    class EarlyFailureTests {
        @Test
        @DisplayName("throw on failure when creating payment")
        void shouldThrowOnFailure_whenCreatingPayment() {
            OrderDTO dto = createValidOrderDTO();
            Order order = mockOrder();

            when(orderFactory.createNewOrderFromDTO(dto)).thenReturn(order);
            doThrow(new PaymentCreationException("Payment creation failure"))
                    .when(paymentService).createPayment(any(Order.class));

            PaymentCreationException ex = assertThrows(
                    PaymentCreationException.class,
                    () -> commandService.placeOrder(dto)
            );

            assertEquals("Payment creation failure", ex.getMessage());
            verify(orderFactory).createNewOrderFromDTO(dto);
            verify(paymentService).createPayment(order);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("")
        void shouldThrow_whenFactoryFails() {
            OrderDTO dto = createValidOrderDTO();

            when(orderFactory.createNewOrderFromDTO(dto)).thenThrow(new IllegalArgumentException("error occurred"));

            assertThrows(IllegalArgumentException.class, () -> commandService.placeOrder(dto));

            verify(orderFactory).createNewOrderFromDTO(dto);
            verify(paymentService, never()).createPayment(any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should fail hne payment not set")
        void shouldFail_whenPaymentNotSet() {
            Order order = mockOrder();
            OrderDTO dto = createValidOrderDTO();

            when(orderFactory.createNewOrderFromDTO(dto)).thenReturn(order);
            doNothing().when(paymentService).createPayment(any(Order.class));

            assertThrows(PaymentCreationException.class, () -> commandService.placeOrder(dto));

            verify(repository, never()).save(any());
        }
    }
}
*/
