package com.example.courier.orderservicetest;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.query.OrderQueryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetOrdersForAdminTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderQueryServiceImpl queryService;

    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;
    @Captor
    ArgumentCaptor<Specification<Order>> specificationArgumentCaptor;

    private final Order ORDER_1 = createOrder(1L, OrderStatus.CONFIRMED);
    private final Order ORDER_2 = createOrder(2L, OrderStatus.CONFIRMED);

    @Nested
    @DisplayName("success tests")
    class SuccessTests {
        @Test
        @DisplayName("successfully returns paginated orders with payments")
        void getAllOrdersForAdmin_success() {
            new MockHelper()
                    .repository(ORDER_1, ORDER_2)
                    .mapper(ORDER_1, ORDER_2);

            var result = queryService.getDetailedOrdersForAdmin(0, 10, null, 1L);

            assertEquals(2, result.data().size());
            assertNotNull(specificationArgumentCaptor.getValue());
            verify(orderRepository, times(1)).findAll(specificationArgumentCaptor.capture(), pageableArgumentCaptor.capture());
            verify(orderMapper, times(2)).toAdminOrderResponseDTO(any());
            assertEquals(0, pageableArgumentCaptor.getValue().getPageNumber());
            assertEquals(10, pageableArgumentCaptor.getValue().getPageSize());
            assertEquals(101, result.data().get(0).id());
            assertEquals(102, result.data().get(1).id());

            Sort.Order sortOrder = pageableArgumentCaptor.getValue().getSort().getOrderFor("createDate");
            assertEquals(Sort.Direction.ASC, sortOrder.getDirection());
        }

        @Test
        @DisplayName("no orders found - returns empty list")
        void getAllOrdersForAdmin_returnsEmptyList() {
            new MockHelper().repository();

            var result = queryService.getDetailedOrdersForAdmin(0, 10, null, null);

            assertEquals(0, result.data().size());
            verify(orderMapper, never()).toAdminOrderResponseDTO(any());
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("unexpected error while mapping to adminOrderResponseDto")
        void getAllOrdersForAdmin_failToMap() {
            new MockHelper().repository(ORDER_1);

            when(orderMapper.toAdminOrderResponseDTO(ORDER_1)).thenThrow(new RuntimeException("Failed to map order: 1"));

            assertThatThrownBy(() -> queryService.getDetailedOrdersForAdmin(0, 10, null, null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to map order: 1");
        }
    }

    private Order createOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);

        order.setPayment(createPayment(order.getId() + 1, order));

        return order;
    }

    class MockHelper {
        MockHelper repository(Order... orders) {
            when(orderRepository.findAll(specificationArgumentCaptor.capture(), pageableArgumentCaptor.capture()))
                    .thenReturn(new PageImpl<>(List.of(orders)));
            return this;
        }

        MockHelper mapper(Order... orders) {
            for (Order order : orders) {
                AdminOrderResponseDTO dto = new AdminOrderResponseDTO(100 + order.getId(), null, null, null, null, null, null, null, null);
                when(orderMapper.toAdminOrderResponseDTO(order)).thenReturn(dto);
            }
            return this;
        }
    }

    private Payment createPayment(Long id, Order order) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setOrder(order);

        return payment;
    }
}
