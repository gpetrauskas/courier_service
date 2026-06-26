/*
package com.example.courier.orderservicetest;

import order.domain.gytis.courier.OrderStatus;
import com.example.courier.domain.DeliveryMethod;
import com.example.courier.domain.Order;
import com.example.courier.dto.mapper.OrderMapper;
import dto.order.rest.in.adapter.gytis.courier.OrderSectionUpdateRequest;
import exception.gytis.courier.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.command.OrderCommandServiceImpl;
import com.example.courier.service.order.query.OrderQueryService;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderSectionUpdateTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderQueryService queryService;
    private Validator validator;

    @InjectMocks
    private OrderCommandServiceImpl commandService;

    private Order order;

    @BeforeEach
    void setUp() {
        DeliveryMethod method = new DeliveryMethod(
                "test", "test description", BigDecimal.valueOf(20), false
        );
        method.setId(1L);

        order = createTestOrder(method);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Order createTestOrder(DeliveryMethod deliveryMethod) {
        Order order = new Order();
        order.setId(1L);
        order.setPreference(deliveryMethod);
        order.setStatus(OrderStatus.valueOf("PENDING"));

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
                DeliveryMethod newMethod = new DeliveryMethod();
                newMethod.setId(Long.valueOf(req.deliveryPreferences()));
                orderToUpdate.setPreference(newMethod);
            }
            return null;
        }).when(orderMapper).updateOrderSectionFromRequest(request, order);
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @BeforeEach
        void commonSetup() {
//            when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        }

        @Test
        @DisplayName("successful status update - pass")
        void orderSectionUpdate_statusUpdateSuccess() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "CONFIRMED", "");

            when(queryService.fetchById(updateRequest.id())).thenReturn(order);
            mockMapperUpdate(updateRequest, order);

            commandService.updateOrderSection(updateRequest);

            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("successful delivery method update")
        void orderSectionUpdate_deliveryMethodUpdateSuccess() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "", "333");

            when(queryService.fetchById(updateRequest.id())).thenReturn(order);
            mockMapperUpdate(updateRequest, order);

            commandService.updateOrderSection(updateRequest);

            assertEquals(Long.parseLong(updateRequest.deliveryPreferences()), order.getPreference().getId());
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("successful status and delivery method update")
        void orderSectionUpdate_statusAndDeliveryMethodUpdateSuccess() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "CONFIRMED", "333");

            when(queryService.fetchById(updateRequest.id())).thenReturn(order);
            mockMapperUpdate(updateRequest, order);

            commandService.updateOrderSection(updateRequest);

            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("should fail when both status and deliveryPreference are empty")
        void shouldFailValidationWhenBothFieldsAreEmpty() {
            OrderSectionUpdateRequest request = new OrderSectionUpdateRequest(1L, "orderSection", null, null);
            Set<ConstraintViolation<OrderSectionUpdateRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("order not found - throws exception")
        void orderSectionUpdate_orderNotFound() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "PENDING", null);

            when(queryService.fetchById(anyLong())).thenThrow(new ResourceNotFoundException("Order was not found"));

            assertThatThrownBy(() -> commandService.updateOrderSection(updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("validation error - method")
        void orderSectionUpdate_invalidDeliveryMethod_throws() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "PENDING", "99");

            when(queryService.fetchById(order.getId())).thenReturn(order);

            assertThatThrownBy(() -> commandService.updateOrderSection(updateRequest))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("already set");
        }

        @Test
        @DisplayName("validation error - status")
        void orderSectionUpdate_invalidStatus_throws() {
            OrderSectionUpdateRequest updateRequest = createSectionUpdateRequest(1L, "notExistent", "99");

            when(queryService.fetchById(order.getId())).thenReturn(order);

            assertThatThrownBy(() -> commandService.updateOrderSection(updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid order status");
        }
    }
}
*/
