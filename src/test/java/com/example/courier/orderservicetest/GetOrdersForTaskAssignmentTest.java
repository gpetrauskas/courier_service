package com.example.courier.orderservicetest;

import com.example.courier.domain.Order;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.query.OrderQueryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FetchAllTaskOrdersByTaskTypeAdminTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderQueryServiceImpl queryService;

    private static final Order o = new Order();
    private static final OrderDTO oDto = new OrderDTO(null, null, null, null, null, null, null);
    private static final Page<Order> mockPage = new PageImpl<>(List.of(o));

    @Nested
    @DisplayName("success tests")
    class SuccessTests {

        @Test
        @DisplayName("success")
        void fetchAll_success() {
            when(orderRepository.findAll(ArgumentMatchers.<Specification<Order>>any(), any(Pageable.class))).thenReturn(mockPage);
            when(orderMapper.toOrderDTO(o)).thenReturn(oDto);

            PaginatedResponseDTO<OrderDTO> result = queryService.getOrdersForTaskAssignment(0, 10, "PICKING_UP");

            assertEquals(1, result.data().size());
            verify(orderMapper, times(1)).toOrderDTO(o);
            assertEquals(oDto, result.data().getFirst());
        }

        @Test
        @DisplayName("pagination test")
        void fetchAll_validPagination() {
            List<Order> orders = IntStream.range(0, 5)
                    .mapToObj(i -> new Order())
                    .toList();

            Page<Order> mockPage = new PageImpl<>(orders, PageRequest.of(1, 2), 10);
            when(orderRepository.findAll(ArgumentMatchers.<Specification<Order>>any(), any(Pageable.class))).thenReturn(mockPage);

            PaginatedResponseDTO<OrderDTO> result = queryService.getOrdersForTaskAssignment(1, 2, "PICKING_UP");

            assertEquals(1, result.currentPage());
            assertEquals(5, result.data().size());
            assertEquals(10, result.totalItems());
            verify(orderMapper, times(5)).toOrderDTO(any(Order.class));
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("invalid task type")
        void fetchAll_invalidTaskType() {
            assertThatThrownBy(() -> queryService.getOrdersForTaskAssignment(0, 10, "invalidType"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid status");

            verify(orderRepository, never()).findAll(ArgumentMatchers.<Specification<Order>>any(), any(Pageable.class));
        }

        @Test
        @DisplayName("status null")
        void fetchAll_statusNull() {
            assertThatThrownBy(() -> queryService.getOrdersForTaskAssignment(0, 10, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Status cannot be null or blank");
        }
    }
}
