package com.example.courier;

import com.example.courier.controller.OrderController;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;
    @Mock
    private AuthService authService;
    @Mock
    private DeliveryMethodService deliveryMethodService;
    @Mock
    private OrderService orderService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testAddOrder_Success() {
        OrderDTO orderDTO = mock(OrderDTO.class);
        User user = mock(User.class);
        BigDecimal shippingCost = new BigDecimal("10");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("myemail@mail.com");
        when(authService.getUserByEmail("myemail@mail.com")).thenReturn(user);
        when(deliveryMethodService.calculateShippingCost(orderDTO)).thenReturn(shippingCost);

        ResponseEntity<?> response = orderController.addOrder(orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order placed successfully. Shipping cost: 10", response.getBody());

        verify(orderService, times(1)).placeOrder(user.getId(), orderDTO);
    }

    @Test
    public void testAddOrder_Failure() {
        OrderDTO orderDTO = mock(OrderDTO.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("myemail@mail.com");
        when(authService.getUserByEmail("myemail.@mail.com")).thenThrow(new RuntimeException());

        ResponseEntity<?> response = orderController.addOrder(orderDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Problem occurred placing order.", response.getBody());
    }
}
