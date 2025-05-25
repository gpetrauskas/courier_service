package com.example.courier;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.order.OrderServiceImpl;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.payment.PaymentService;
import com.example.courier.validation.DeliveryOptionValidator;
import com.example.courier.validation.adminorderupdate.OrderUpdateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AuthService authService;
    @Mock
    private OrderUpdateValidator orderUpdateValidator;
    @Mock
    private DeliveryOptionValidator deliveryOptionValidator;
    @Mock
    private ParcelService parcelService;
    @Mock
    private AddressService addressService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private DeliveryMethodService deliveryMethodService;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Order testOrder;
    private Payment testPayment;
    private OrderDTO testOrderDTO;
    private Long testUserId;
    private BigDecimal shoppingCost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUserId = 123L;
        testUser.setEmail("test@exmpl.com");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreateDate(LocalDateTime.now());

        Parcel parcel = new Parcel();
        parcel.setId(1L);
        parcel.setStatus(ParcelStatus.WAITING_FOR_PAYMENT);
        testOrder.setParcelDetails(parcel);

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setStatus(PaymentStatus.NOT_PAID);
        testPayment.setOrder(testOrder);

        testOrderDTO = new OrderDTO(
                null,
                new AddressDTO(null, "city", "streetsend", "123456789", "321", "370111", "12345", "name sender"),
                new AddressDTO(null, "city", "streerecip", "987654321", "123", "370000", "54321", "name recipient"),
                new ParcelDTO(null, "weight", "dimensions", "contents", null, ParcelStatus.WAITING_FOR_PAYMENT),
                "deliveryMethod",
                OrderStatus.PENDING,
                LocalDateTime.now()
        );

        OrderAddress senderAddress = new OrderAddress();
        OrderAddress recipientAddress = new OrderAddress();
        shoppingCost = new BigDecimal("10");

        when(authService.getUserById(testUserId)).thenReturn(testUser);
        when(orderMapper.toOrder(testOrderDTO)).thenReturn(testOrder);
        when(addressService.fetchOrCreateOrderAddress(any(), any()))
                .thenReturn(senderAddress)
                .thenReturn(recipientAddress);
        when(deliveryMethodService.calculateShippingCost(any())).thenReturn(shoppingCost);
        when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @Test
        @DisplayName("Should successfully place order")
        void testPlaceOrder_Success() {
            Long orderId = orderService.placeOrder(testUserId, testOrderDTO);

            assertNotNull(orderId);
            assertEquals(1L , orderId);

            verify(authService).getUserById(testUserId);
            verify(addressService, times(2)).fetchOrCreateOrderAddress(any(), any());
            verify(deliveryMethodService).calculateShippingCost(testOrderDTO);
            verify(paymentService).createPayment(any(), eq(shoppingCost));
            verify(orderRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Failure tests")
    class FailureTests {
        @Test
        @DisplayName("place order - wrong userId, should fail")
        void placeOrder_wrongUserId_shouldFail() {
            when(authService.getUserById(testUserId)).thenThrow(new ResourceNotFoundException("User not found"));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                    orderService.placeOrder(testUserId, testOrderDTO));

            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("place order - invalid delivery method, should fail")
        void placeOrder_invalidDeliveryMethod_shouldFail() {
            OrderDTO invalidOrderDTO = new OrderDTO(
                    null,
                    testOrderDTO.senderAddress(),
                    testOrderDTO.recipientAddress(),
                    testOrderDTO.parcelDetails(),
                    "nonexistentMethod",
                    OrderStatus.PENDING,
                    LocalDateTime.now()
            );


        }
    }
}
