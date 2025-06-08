package com.example.courier.OrderServiceTest;

import com.example.courier.domain.User;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.OrderServiceImpl;
import com.example.courier.service.payment.PaymentService;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CancelOrderTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private CurrentPersonService currentPersonService;
    @InjectMocks
    OrderServiceImpl orderService;

    private User testUser;
    

    @Nested
    @DisplayName("success tests")
    class SuccessTests {
        @BeforeEach
        void setUp() {

        }

        @Test
        @DisplayName("cancel order - should success")
        void cancelOrder_shouldSuccess() {

        }
    }

}
