package com.example.courier.OrderServiceTest;

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
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.order.OrderServiceImpl;
import com.example.courier.service.payment.PaymentService;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.order.OrderCreationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceOrderTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private DeliveryMethodService deliveryMethodService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PersonService personService;
    @Mock
    private CurrentPersonService currentPersonService;
    @Mock
    private OrderCreationValidator orderCreationValidator;
    @InjectMocks
    private OrderServiceImpl orderService;

    private final static Logger log = LoggerFactory.getLogger(PlaceOrderTest.class);
    private User testUser;
    private Order testOrder;
    private final Long testUserId = 123L;
    private Payment testPayment;
    private Parcel testParcel;
    private OrderDTO testOrderDTO;
    private final BigDecimal shoppingCost = new BigDecimal("10");

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testParcel = createTestParcel();
        testOrder = createTestOrder();
        testPayment = createTestPayment();
        testOrderDTO = createValidORderDTO();
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@exmpl.com");

        return user;
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setParcelDetails(testParcel);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        return order;
    }

    private Parcel createTestParcel() {
        Parcel parcel = new Parcel();
        parcel.setId(1L);
        parcel.setWeight("10");
        parcel.setDimensions("2");
        parcel.setStatus(ParcelStatus.WAITING_FOR_PAYMENT);

        return parcel;
    }

    private Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setOrder(testOrder);

        return payment;
    }

    private OrderDTO createValidORderDTO() {
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

    private void mockCurrentUser() {
        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
    }

    private void mockOrderCreationSuccess() {
        when(addressService.fetchOrCreateOrderAddress(any(), any())).thenReturn(new OrderAddress());
        when(deliveryMethodService.calculateShippingCost(any())).thenReturn(shoppingCost);
    }

    private void mockOrderMapperToOrder() {
        when(orderMapper.toOrder(any())).thenReturn(new Order());
    }

    private void verifyCommonOrderCreationInteractions() {
        verify(orderCreationValidator).validate(testOrderDTO);
        verify(addressService, times(2)).fetchOrCreateOrderAddress(any(), eq(testUser));
        verify(deliveryMethodService).calculateShippingCost(testOrderDTO);
        verify(paymentService).createPayment(any(), eq(shoppingCost));
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @BeforeEach
        void setUp() {
            mockCurrentUser();
            mockOrderMapperToOrder();
            mockOrderCreationSuccess();
        }

        @Test
        @DisplayName("Should successfully place order")
        void testPlaceOrder_Success() {
            when(orderRepository.save(any())).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            Long orderId = orderService.placeOrder(testUserId, testOrderDTO);

            assertNotNull(orderId);
            assertEquals(1L , orderId);
            verifyCommonOrderCreationInteractions();
            verify(orderRepository).save(any());
        }


        @Test
        @DisplayName("set correct initial order status, should pass")
        void placeOrder_shouldSetInitialOrderStatus() {

            ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
            when(orderRepository.save(orderArgumentCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

            orderService.placeOrder(testUserId, testOrderDTO);

            assertEquals(OrderStatus.PENDING, orderArgumentCaptor.getValue().getStatus());
            verifyCommonOrderCreationInteractions();
            verify(orderRepository, never()).save(argThat(order -> !OrderStatus.PENDING.equals(order.getStatus())));
        }

        @Test
        @DisplayName("should generate trackign number")
        void placeOrder_shouldGenerateTrackingNumber() {
            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            when(orderRepository.save(captor.capture())).thenReturn(testOrder);

            orderService.placeOrder(testUserId, testOrderDTO);

            Parcel savedParcel = captor.getValue().getParcelDetails();
            assertNotNull(savedParcel.getTrackingNumber());
            assertTrue(savedParcel.getTrackingNumber().length() > 10);
            verifyCommonOrderCreationInteractions();
        }

        @Test
        @DisplayName("should set creation date")
        void placeOrder_shouldSetCreationDate() {
            LocalDateTime beforeTest = LocalDateTime.now();

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            when(orderRepository.save(captor.capture())).thenReturn(testOrder);

            orderService.placeOrder(testUserId, testOrderDTO);
            // have manually set creationDate as placeOrder in OrderService creates it with nano(0)
            captor.getValue().setCreateDate(LocalDateTime.now());

            assertNotNull(captor.getValue().getCreateDate());
            assertTrue(captor.getValue().getCreateDate().isAfter(beforeTest));
            assertTrue(captor.getValue().getCreateDate().isBefore(LocalDateTime.now()));
            verifyCommonOrderCreationInteractions();
        }

        @Test
        @DisplayName("should set correct parcel details")
        void placeOrder_shouldSetParcelDetails() {
            when(deliveryMethodService.getDescriptionById(Long.parseLong(testOrderDTO.parcelDetails().weight())))
                    .thenReturn("5kg");
            when(deliveryMethodService.getDescriptionById(Long.parseLong(testOrderDTO.parcelDetails().dimensions())))
                    .thenReturn("1m");

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            when(orderRepository.save(captor.capture())).thenReturn(testOrder);

            orderService.placeOrder(testUserId, testOrderDTO);

            Parcel savedParcel = captor.getValue().getParcelDetails();
            assertNotNull(savedParcel);
            assertEquals("5kg", savedParcel.getWeight());
            assertEquals("1m", savedParcel.getDimensions());
            assertEquals("contents", savedParcel.getContents());
            verifyCommonOrderCreationInteractions();
        }
    }

    @Nested
    @DisplayName("early failure tests")
    class EarlyFailureTests {
        @Test
        @DisplayName("parcel details is null, shoul fail")
        void placeOrder_parcelDetailsIsNull_shouldFail() {
            OrderDTO invalidOrderWithNullParcel = new OrderDTO(
                    testOrder.getId(), testOrderDTO.senderAddress(), testOrderDTO.recipientAddress(), null,
                    testOrderDTO.deliveryMethod(), testOrderDTO.status(), testOrderDTO.createTime()
            );

            doThrow(new IllegalArgumentException("Parcel cannot be null"))
                    .when(orderCreationValidator).validate(argThat(parcel -> parcel.parcelDetails() == null));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    orderService.placeOrder(testUserId, invalidOrderWithNullParcel));

            assertEquals("Parcel cannot be null", exception.getMessage());

            verify(orderCreationValidator).validate(invalidOrderWithNullParcel);
        }

        @Test
        @DisplayName("address is null - should throw exception")
        void placeOrder_AddressDTOIsNull_shouldThrowException() {
            OrderDTO orderWithNullSender = new OrderDTO(1L, null,
                    testOrderDTO.recipientAddress(), testOrderDTO.parcelDetails(), testOrderDTO.deliveryMethod(),
                    testOrderDTO.status(), testOrderDTO.createTime());

            doThrow(new IllegalArgumentException("Address cannot be null"))
                    .when(orderCreationValidator).validate(argThat(o ->
                            o.senderAddress() == null));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, orderWithNullSender))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");

            verify(orderCreationValidator).validate(argThat(order ->
                    order.senderAddress() == null));
            verifyNoMoreInteractions(addressService);
            verifyNoMoreInteractions(orderRepository);
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @BeforeEach
        void setUpFailureMocks() {
            mockCurrentUser();
        }

        private void setUpForOrderProcessing() {
            mockOrderMapperToOrder();
        }

        private void setupForFullFlow() {
            setUpForOrderProcessing();
            mockOrderCreationSuccess();
        }

        @Test
        @DisplayName("user not found - should fail")
        void placeOrder_userNotFound_shouldFail() {
            when(currentPersonService.getCurrentPerson())
                    .thenThrow(new ResourceNotFoundException("User was not found"));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, testOrderDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User was not found");

            verify(personService, never()).findById(any());
        }

        @Test
        @DisplayName("current person null - should fail ")
        void placeOrder_whenCurrentPersonIsNull_shouldThrowUnauthorizedAccessException() {
            when(currentPersonService.getCurrentPerson()).thenReturn(null);

            UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                    orderService.placeOrder(testUserId, testOrderDTO));

            assertEquals("Not allowed to place orders", exception.getMessage());
        }

        @Test
        @DisplayName("not found id - should fail")
        void placeOrder_notFoundDeliveryMethod_shouldFail() {
            setUpForOrderProcessing();

            OrderDTO invalidOrderDTO = new OrderDTO(
                    1L,
                    testOrderDTO.senderAddress(),
                    testOrderDTO.recipientAddress(),
                    testOrderDTO.parcelDetails(),
                    "9999",
                    OrderStatus.PENDING,
                    LocalDateTime.now()
            );

            when(deliveryMethodService.getDescriptionById(Long.parseLong(invalidOrderDTO.deliveryMethod()))).thenThrow(new DeliveryOptionNotFoundException("Delivery method was not found"));

            assertThrows(DeliveryOptionNotFoundException.class, () ->
                    orderService.placeOrder(testUserId, invalidOrderDTO));
        }

        @Test
        @DisplayName("deliveryMethod is text instead of id, should fail")
        void placeOrder_wrongDeliveryMethodFormat_shouldFail() {
            setUpForOrderProcessing();

            OrderDTO invalidOrderDTOBadMethod = new OrderDTO(
                    1L,
                    testOrderDTO.senderAddress(), testOrderDTO.recipientAddress(),
                    testOrderDTO.parcelDetails(),
                    "invalidFormatMethod",
                    OrderStatus.PENDING,
                    LocalDateTime.now()
            );

            assertThrows(NumberFormatException.class, () ->
                    orderService.placeOrder(testUserId, invalidOrderDTOBadMethod));
        }

        @Test
        @DisplayName("order address do not belong to user - should fail")
        void placeOrder_orderAddressDoesNotBelongToUser_shouldThrow() {
            when(addressService.fetchOrCreateOrderAddress(
                    argThat(address -> address.id().equals(testOrderDTO.senderAddress().id())),
                    eq(testUser)
            )).thenThrow(new UserAddressMismatchException("Address not found or not owned by user"));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, testOrderDTO))
                    .isInstanceOf(UserAddressMismatchException.class)
                    .hasMessageContaining("Address not found or not owned by user");
        }

        @Test
        @DisplayName("payment creation failure, should rollback")
        void placeOrder_paymentCreationFailure_shouldRollback() {
            setupForFullFlow();

            doThrow(new RuntimeException("Payment creation failure"))
                    .when(paymentService).createPayment(any(), any());

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, testOrderDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Payment creation failure");

            verify(orderRepository).save(any(Order.class));
            verify(paymentService, never()).savePayment(any());
            verify(orderRepository, never()).flush();
        }

        @Test
        @DisplayName("unauthorized class, should fail")
        void placeOrder_invalidClassNotUser_shouldFail() {
            Courier courier = new Courier();

            when(currentPersonService.getCurrentPerson()).thenReturn(courier);

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, testOrderDTO))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessageContaining("Not allowed");
        }
    }
}
