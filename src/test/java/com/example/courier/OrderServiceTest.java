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
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.DeliveryOptionRepository;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.order.OrderServiceImpl;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.payment.PaymentService;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private AddressRepository addressRepository;
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
    @Mock
    private DeliveryOptionRepository deliveryOptionRepository;
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
        parcel.setWeight("10");
        parcel.setDimensions("2");
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
                new ParcelDTO(1L, "5", "1", "contents", null, ParcelStatus.WAITING_FOR_PAYMENT),
                "1",
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
        shoppingCost = new BigDecimal("10");
        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @Test
        @DisplayName("Should successfully place order")
        void testPlaceOrder_Success() {
            OrderAddress senderAddress = new OrderAddress();
            OrderAddress recipientAddress = new OrderAddress();

            when(orderMapper.toOrder(testOrderDTO)).thenReturn(testOrder);
            when(addressService.fetchOrCreateOrderAddress(eq(testOrderDTO.senderAddress()), eq(testUser)))
                    .thenReturn(senderAddress);
            when(addressService.fetchOrCreateOrderAddress(eq(testOrderDTO.recipientAddress()), eq(testUser)))
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

            verify(addressService, times(2)).fetchOrCreateOrderAddress(any(), eq(testUser));

            verify(deliveryMethodService).calculateShippingCost(testOrderDTO);
            verify(addressService).fetchOrCreateOrderAddress(eq(testOrderDTO.senderAddress()), eq(testUser));
            verify(addressService).fetchOrCreateOrderAddress(eq(testOrderDTO.recipientAddress()), eq(testUser));
            verify(paymentService).createPayment(any(), eq(shoppingCost));
            verify(orderCreationValidator).validate(testOrderDTO);
            verify(orderRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Failure tests")
    class FailureTests {
        @Test
        @DisplayName("place order - user not found, should fail")
        void placeOrder_userNotFound_shouldThrowResourceNotFoundException() {
            when(currentPersonService.getCurrentPerson()).thenThrow(new ResourceNotFoundException("User was not found"));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, testOrderDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User was not found");

            verify(currentPersonService, times(1)).getCurrentPerson();
            verify(personService, never()).findById(testUserId);

        }

        @Test
        @DisplayName("place order - wrong userId, should fail")
        void placeOrder_whenCurrentPersonIsNull_shouldThrowUnauthorizedAccessException() {
            when(currentPersonService.getCurrentPerson()).thenReturn(null);

            UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                    orderService.placeOrder(testUserId, testOrderDTO));

            assertEquals("Not allowed to place orders", exception.getMessage());
        }

        @Test
        @DisplayName("place order - invalid delivery method, should fail")
        void placeOrder_invalidDeliveryMethod_shouldFail() {
            OrderDTO invalidOrderDTO = new OrderDTO(
                    1L,
                    testOrderDTO.senderAddress(),
                    testOrderDTO.recipientAddress(),
                    testOrderDTO.parcelDetails(),
                    "9999",
                    OrderStatus.PENDING,
                    LocalDateTime.now()
            );

            when(orderMapper.toOrder(any())).thenReturn(testOrder);
            when(deliveryMethodService.getDescriptionById(9999L)).thenThrow(new DeliveryOptionNotFoundException("Delivery option not found"));

            assertThrows(DeliveryOptionNotFoundException.class, () ->
                    orderService.placeOrder(testUserId, invalidOrderDTO));
        }

        @Test
        @DisplayName("place order - deliveryMethod is text instead of ID, should fail")
        void placeOrder_invalidDeliveryMethodText_shouldFail() {
            OrderDTO invalidOrderDTO = new OrderDTO(
                    1L,
                    testOrderDTO.senderAddress(),
                    testOrderDTO.recipientAddress(),
                    testOrderDTO.parcelDetails(),
                    "invalidMethod",
                    OrderStatus.PENDING,
                    LocalDateTime.now()
            );

            when(orderMapper.toOrder(any(OrderDTO.class))).thenReturn(testOrder);

            assertThrows(NumberFormatException.class, () ->
                    orderService.placeOrder(testUserId, invalidOrderDTO));
        }

        @Test
        @DisplayName("place order - payment creation fails, should throw exception")
        void placeOrder_paymentCreationFails_shouldThrow() {
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

            verify(paymentService).createPayment(any(), eq(shoppingCost));
        }

        @Test
        @DisplayName("place order - address is null, should throw exception")
        void placeOrder_addressDTONull_shouldThrowException() {
            OrderDTO orderWithNullSender = createTestOrderDTO(1L, null,
                    testOrderDTO.recipientAddress(), testOrderDTO.parcelDetails(), testOrderDTO.deliveryMethod(),
                    testOrderDTO.status(), testOrderDTO.createTime());

            when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(addressService.fetchOrCreateOrderAddress(orderWithNullSender.senderAddress(), testUser))
                    .thenThrow(new IllegalArgumentException("Address cannot be null"));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, orderWithNullSender))
                    .isInstanceOf(IllegalArgumentException.class)
                            .hasMessageContaining("cannot be null");

            verify(orderCreationValidator).validate(argThat(order ->
                    order.senderAddress() == null));
            verifyNoMoreInteractions(addressService);
            verifyNoMoreInteractions(orderRepository);
        }

        @Test
        @DisplayName("place order - address does not belong to user, shoudl fail")
        void placeOrder_addressDoesNotBelongToUser() {
            when(addressService.fetchOrCreateOrderAddress(any(), any()))
                    .thenThrow(new UserAddressMismatchException("Address doest not belong to the user"));

            assertThrows(UserAddressMismatchException.class, () ->
                    orderService.placeOrder(testUserId, testOrderDTO));
        }

        @Test
        @DisplayName("place order - invalid delivery method, should fail")
        void placeOrder_withInvalidDeliverMethod_shouldThrowException() {
            OrderDTO invalidOrderDTO = new OrderDTO(1L, testOrderDTO.senderAddress(), testOrderDTO.recipientAddress(),
                    testOrderDTO.parcelDetails(), "999", OrderStatus.PENDING, LocalDateTime.now());

            when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(orderMapper.toOrder(any(OrderDTO.class))).thenReturn(new Order());
            when(deliveryMethodService.getDescriptionById(Long.parseLong(invalidOrderDTO.deliveryMethod())))
                    .thenThrow(new DeliveryOptionNotFoundException("Delivery option not found"));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, invalidOrderDTO))
                    .isInstanceOf(DeliveryOptionNotFoundException.class)
                    .hasMessage("Delivery option not found");
        }

        @Test
        @DisplayName("placeOrder - should throw UserAddressMismatchException if address not owned by user")
        void placeOrder_shouldThrowIfAddressNotOwnedByUser() {
            when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(addressService.fetchOrCreateOrderAddress(
                    argThat(address -> address.equals(testOrderDTO.senderAddress())),
                    eq(testUser)))
                    .thenThrow(new UserAddressMismatchException("Address does not belong to user"));

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, testOrderDTO))
                    .isInstanceOf(UserAddressMismatchException.class)
                    .hasMessageContaining("does not belong to user");

            verify(orderCreationValidator).validate(testOrderDTO);
            verify(addressService).fetchOrCreateOrderAddress(eq(testOrderDTO.senderAddress()), eq(testUser));
            verifyNoMoreInteractions(orderRepository);
            verifyNoInteractions(paymentService, deliveryMethodService);
        }

        @Test
        @DisplayName("placeorder - parcel details is null, should throw")
        void placeOrder_parcelDetailsNull_shouldThrow() {
            OrderDTO orderDTOWithNullParcel = new OrderDTO(
                    1L, testOrderDTO.senderAddress(), testOrderDTO.recipientAddress(), null,
                    testOrderDTO.deliveryMethod(), OrderStatus.PENDING, LocalDateTime.now()
            );

            lenient().when(currentPersonService.getCurrentPerson()).thenReturn(testUser);

            doThrow(new IllegalArgumentException("Parcel details cannot be null"))
                    .when(orderCreationValidator).validate(orderDTOWithNullParcel);

            assertThatThrownBy(() -> orderService.placeOrder(testUserId, orderDTOWithNullParcel))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Parcel details cannot be null");

            verify(orderCreationValidator).validate(orderDTOWithNullParcel);
            verifyNoInteractions(orderMapper, orderRepository, addressService, deliveryMethodService, paymentService);
        }

        @Test
        @DisplayName("placeOrder - should rollback transaction if payent not created")
        void placeOrder_shouldRollbackOnPaymentCreationFailure() {

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
