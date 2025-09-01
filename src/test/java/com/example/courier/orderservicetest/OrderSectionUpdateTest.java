package com.example.courier.orderservicetest;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.DeliveryOptionValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderSectionUpdateTest {
    @Mock
    private DeliveryOptionValidator deliveryOptionValidator;
    @Mock
    private OrderUpdateValidator orderUpdateValidator;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderMapper orderMapper;
    @Mock
    private CurrentPersonService currentPersonService;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = createTestOrder("111", "PENDING");
    }

    private Order createTestOrder(String deliveryMethodId, String status) {
        Order order = new Order();
        order.setId(1L);
        order.setPreference(deliveryMethodId);
        order.setStatus(OrderStatus.valueOf(status));

        return order;
    }

    private OrderSectionUpdateRequest createSectionUpdateRequest(Long id, String status, String deliveryMethod ) {
        return new OrderSectionUpdateRequest(id, "orderSection", status, deliveryMethod);
    }

    private void mockMapperUpdate(OrderSectionUpdateRequest request, Order order) {
        doAnswer(inv -> {
            OrderSectionUpdateRequest req = inv.getArgument(0);
            Order orderToUpdate = inv.getArgument(1);

            if (req.status() != null && !req.status().isEmpty()) {
                orderToUpdate.setStatus(OrderStatus.valueOf(req.status()));
            }
            if (req.deliveryPreferences() != null && !req.deliveryPreferences().isEmpty()) {
                orderToUpdate.setPreference(req.deliveryPreferences());
            }
            return null;
        }).when(orderMapper).updateOrderSectionFromRequest(request, order);
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @BeforeEach
        void commonSetup() {
            when(currentPersonService.isAdmin()).thenReturn(true);
            when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        }

        @Test
        @DisplayName("successful status update - pass")
        void orderSectionUpdate_statusUpdateSuccess() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "CONFIRMED", "");

            mockMapperUpdate(updateRequest, order);

            orderService.orderSectionUpdate(updateRequest);

            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("successful delivery method update")
        void orderSectionUpdate_deliveryMethodUpdateSuccess() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "", "333");

            mockMapperUpdate(updateRequest, order);

            orderService.orderSectionUpdate(updateRequest);

            assertEquals(updateRequest.deliveryPreferences(), order.getPreference());
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("successful status and delivery method update")
        void orderSectionUpdate_statusAndDeliveryMethodUpdateSuccess() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "CONFIRMED", "333");

            mockMapperUpdate(updateRequest, order);

            orderService.orderSectionUpdate(updateRequest);

            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("no update when status and method both empty")
        void orderSelectionUpdate_noChanges_noSave() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "", "");

            mockMapperUpdate(updateRequest, order);

            orderService.orderSectionUpdate(updateRequest);

            assertEquals(OrderStatus.PENDING, order.getStatus());
            assertEquals("111", order.getPreference());
            verify(orderRepository).save(order);
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("current user not admin - throws exception")
        void orderSectionUpdate_currentUserIsNotAdmin() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "", "111");

            when(currentPersonService.isAdmin()).thenReturn(false);

            assertThatThrownBy(() -> orderService.orderSectionUpdate(updateRequest))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("Only ADMIN can update Order section");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("order not found - throws exception")
        void orderSectionUpdate_orderNotFound() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "", "");

            when(currentPersonService.isAdmin()).thenReturn(true);
            when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.orderSectionUpdate(updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("validation error - method")
        void orderSectionUpdate_invalidDeliveryMethod_throws() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "PENDING", "99");

            when(currentPersonService.isAdmin()).thenReturn(true);
            when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

            doThrow(new ValidationException("Delivery method is already set to..."))
                    .when(deliveryOptionValidator)
                    .validateDeliveryPrefMethodUpdate(updateRequest, order);

            assertThatThrownBy(() -> orderService.orderSectionUpdate(updateRequest))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("already set");
        }

        @Test
        @DisplayName("validation error - status")
        void orderSectionUpdate_invalidStatus_throws() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "notExistent", "99");

            when(currentPersonService.isAdmin()).thenReturn(true);
            when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

            doThrow(new IllegalArgumentException("Invalid order status"))
                    .when(orderUpdateValidator)
                    .validateOrderSectionStatusUpdate(updateRequest, order);

            assertThatThrownBy(() -> orderService.orderSectionUpdate(updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid order status");
        }
    }
}
