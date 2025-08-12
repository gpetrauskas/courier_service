package com.example.courier.orderservicetest;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.payment.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetOrdersForAdminTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private final Order ORDER_1 = createOrder(1L, OrderStatus.CONFIRMED);
    private final Order ORDER_2 = createOrder(2L, OrderStatus.CONFIRMED);
    private final Payment PAYMENT_1 = createPayment(1L, ORDER_1);
    private final Payment PAYMENT_2 = createPayment(1L, ORDER_2);

    private Order createOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);

        return order;
    }

    class MockHelper {
        MockHelper repository(Order... orders) {
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(orders)));
            return this;
        }

        MockHelper paymentService(Payment... payments) {
            Map<Long, Payment> paymentMap = Arrays.stream(payments)
                            .collect(Collectors.toMap(p -> p.getOrder().getId(), p -> p));

            when(paymentService.getPaymentsForOrders(anyList())).thenReturn(paymentMap);
            return this;
        }

        MockHelper orderMapper(Payment... payments) {
            for (Payment payment : payments) {
                Order order = payment.getOrder();
                Long dtoId = order.getId() + 10;
                when(orderMapper.toAdminOrderResponseDTO(order, payment)).thenReturn(createFakeDTO(dtoId));
            }
            return this;
        }
    }

    @Nested
    @DisplayName("success tests")
    class SuccessTests {
        @Test
        @DisplayName("successfully returns paginated orders with payments")
        void getAllOrdersForAdmin_success() {
            new MockHelper()
                    .repository(ORDER_1, ORDER_2)
                    .paymentService(PAYMENT_1, PAYMENT_2)
                    .orderMapper(PAYMENT_1, PAYMENT_2);

            var result = orderService.getDetailedOrdersForAdmin(0, 10, null, null);

            assertEquals(2, result.data().size());
            verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));
            verify(paymentService).getPaymentsForOrders(List.of(1L, 2L));
            verify(orderMapper).toAdminOrderResponseDTO(ORDER_1, PAYMENT_1);
            verify(orderMapper).toAdminOrderResponseDTO(ORDER_2, PAYMENT_2);
        }

        @Test
        @DisplayName("no orders found - returns empty list")
        void getAllOrdersForAdmin_returnsEmptyList() {
            new MockHelper().repository();

            var result = orderService.getDetailedOrdersForAdmin(0, 10, null, null);

            assertEquals(0, result.data().size());
            verify(paymentService, never()).getPaymentsForOrders(anyList());
            verify(orderMapper, never()).toAdminOrderResponseDTO(any(), any());
        }

        @Test
        @DisplayName("filter by status")
        void getAllOrdersForAdmin_filterByStatus() {
            AdminOrderResponseDTO expectedDto = createFakeDTO(ORDER_1.getId() + 10);

            new MockHelper().repository(ORDER_1).paymentService(PAYMENT_1).orderMapper(PAYMENT_1);

            var results = orderService.getDetailedOrdersForAdmin(0, 10, "CONFIRMED", null);

            assertEquals(1, results.data().size());
            assertEquals(expectedDto, results.data().getFirst());
            verify(paymentService).getPaymentsForOrders(List.of(ORDER_1.getId()));
        }

        @Test
        @DisplayName("filter by user id")
        void getAllOrdersForAdmin_filterByUserId() {
            AdminOrderResponseDTO expectedDto = createFakeDTO(ORDER_1.getId() + 10);

            new MockHelper().repository(ORDER_1).paymentService(PAYMENT_1).orderMapper(PAYMENT_1);

            var results = orderService.getDetailedOrdersForAdmin(0, 10, null, 1L);

            assertEquals(1, results.data().size());
            assertEquals(expectedDto.id(), results.data().getFirst().id());

        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("payment not found")
        void getAllOrdersForAdmin_paymentNotFound() {
            new MockHelper().repository(ORDER_1);
            when(paymentService.getPaymentsForOrders(List.of(ORDER_1.getId()))).thenThrow(new ResourceNotFoundException("not found"));

            assertThatThrownBy(() -> orderService.getDetailedOrdersForAdmin(0, 10, null, null))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");

            verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            verify(orderMapper, never()).toAdminOrderResponseDTO(any(), any());
        }

        @Test
        @DisplayName("unexcpected error while mapping to adminOrderResponseDto")
        void getAllOrdersForAdmin_failToMap() {
            new MockHelper().repository(ORDER_1).paymentService(PAYMENT_1);

            when(orderMapper.toAdminOrderResponseDTO(ORDER_1, PAYMENT_1)).thenThrow(new RuntimeException("Failed to map order: 1"));

            assertThatThrownBy(() -> orderService.getDetailedOrdersForAdmin(0, 10, null, null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to map order: 1");
        }
    }

    private Payment createPayment(Long id, Order order) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setOrder(order);

        return payment;
    }

    private AdminOrderResponseDTO createFakeDTO(Long id) {
        LocalDateTime fixedDate = LocalDateTime.of(2001,1, 1, 0, 0);
        return new AdminOrderResponseDTO(
                id, null, null, null, "1",
                null, OrderStatus.CONFIRMED, fixedDate, null
        );
    }
}
