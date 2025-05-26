package com.example.courier;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.order.OrderServiceImpl;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.payment.PaymentService;
import com.example.courier.validation.DeliveryOptionValidator;
import com.example.courier.validation.adminorderupdate.OrderUpdateValidator;
import com.example.courier.validation.order.OrderCreationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    @Mock
    private OrderCreationValidator orderCreationValidator;
    @InjectMocks
    private OrderServiceImpl orderService;

    private final static Logger log = LoggerFactory.getLogger(OrderServiceTest.class);
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
                1L,
                new AddressDTO(1L, "city", "streetsend", "123456789", "321", "370111", "12345", "name sender"),
                new AddressDTO(2L, "city", "streerecip", "987654321", "123", "370000", "54321", "name recipient"),
                new ParcelDTO(1L, "weight", "dimensions", "contents", null, ParcelStatus.WAITING_FOR_PAYMENT),
                "deliveryMethod",
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
        shoppingCost = new BigDecimal("10");
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @Test
        @DisplayName("Should successfully place order")
        void testPlaceOrder_Success() {
            OrderAddress senderAddress = new OrderAddress();
            OrderAddress recipientAddress = new OrderAddress();

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

            when(authService.getUserById(testUserId)).thenReturn(testUser);
            when(orderMapper.toOrder(any())).thenReturn(testOrder);
            when(deliveryMethodService.calculateShippingCost(invalidOrderDTO))
                    .thenThrow(new DeliveryOptionNotFoundException("Delivery option not found"));

            DeliveryOptionNotFoundException exception = assertThrows(DeliveryOptionNotFoundException.class, () ->
                    orderService.placeOrder(testUserId, invalidOrderDTO));

            assertEquals("Delivery option not found", exception.getMessage());
        }

        @Test
        @DisplayName("place order - payment creation fails, should throw exception")
        void placeOrder_paymentCreationFails_shouldThrow() {
            when(authService.getUserById(testUserId)).thenReturn(testUser);
            when(orderMapper.toOrder(testOrderDTO)).thenReturn(testOrder);
            when(addressService.fetchOrCreateOrderAddress(any(), any()))
                    .thenReturn(new OrderAddress())
                    .thenReturn(new OrderAddress());
            when(deliveryMethodService.calculateShippingCost(any())).thenReturn(shoppingCost);
            when(orderRepository.save(any())).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            doThrow(new RuntimeException("Payment creation failed"))
                    .when(paymentService).createPayment(any(), eq(shoppingCost));

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.placeOrder(testUserId, testOrderDTO));

            assertEquals("Payment creation failed", exception.getMessage());

            verify(authService).getUserById(testUserId);
            verify(paymentService).createPayment(any(), eq(shoppingCost));
        }

        @Test
        @DisplayName("place order - address is null, should throw exception")
        void placeOrder_addressDTONull_shouldThrowException() {
            OrderDTO orderWithNullSender = createTestOrderDTO(1L, null,
                    testOrderDTO.recipientAddress(), testOrderDTO.parcelDetails(), testOrderDTO.deliveryMethod(),
                    testOrderDTO.status(), testOrderDTO.createTime());

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, orderWithNullSender))
                    .isInstanceOf(IllegalArgumentException.class)
                            .hasMessageContaining("cannot be null");

            verify(orderCreationValidator).validate(argThat(order ->
                    order.senderAddress() == null));


        }

        @Test
        @DisplayName("place order - address does not belong to user, shoudl fail")
        void placeOrder_addressDoesNotBelongToUser() {
            when(authService.getUserById(testUserId)).thenReturn(testUser);
            when(addressService.fetchOrCreateOrderAddress(any(), any()))
                    .thenThrow(new UserAddressMismatchException("Address doest not belong to the user"));

            assertThrows(UserAddressMismatchException.class, () ->
                    orderService.placeOrder(testUserId, testOrderDTO));
        }
    }

    private OrderDTO createTestOrderDTO(
            Long id, AddressDTO senderAddress, AddressDTO recipientAddress,
            ParcelDTO parcelDetails, String deliveryMethod, OrderStatus status,
            LocalDateTime createDate
    ) {
        return new OrderDTO(id, senderAddress, recipientAddress, parcelDetails, deliveryMethod, status, createDate);
    }

}
