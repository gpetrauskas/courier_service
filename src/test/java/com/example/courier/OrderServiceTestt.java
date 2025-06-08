package com.example.courier;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.*;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTestt {
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

    private final static Logger log = LoggerFactory.getLogger(OrderServiceTestt.class);
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

    private void mockCurrentUserId() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(testUserId);
    }

    private void mockOrderCreationSuccess() {
        when(addressService.fetchOrCreateOrderAddress(any(), any())).thenReturn(new OrderAddress());
        when(deliveryMethodService.calculateShippingCost(any())).thenReturn(shoppingCost);
    }

    private void mockOrderMapperToOrder() {
        when(orderMapper.toOrder(any())).thenReturn(new Order());
    }

    @Nested
    @DisplayName("success tests")
    class SuccessfulTests {
        @Test
        @DisplayName("cancelOrder - should pass")
        void cancelOrder_validData_shouldPass() {
            lenient().when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(currentPersonService.getCurrentPersonId()).thenReturn(testUserId);
            when(orderRepository.findByIdAndUserId(testOrder.getId(), testUserId)).thenReturn(Optional.of(testOrder));
            when(paymentService.getPaymentByOrderId(testOrder.getId())).thenReturn(testPayment);

            orderService.cancelOrder(testOrder.getId());

            assertEquals(testOrder.getStatus(), OrderStatus.CANCELED);
            assertEquals(testOrder.getParcelDetails().getStatus(), ParcelStatus.CANCELED);
            assertEquals(testPayment.getStatus(), PaymentStatus.CANCELED);
        }
    }

    @Nested
    @DisplayName("Failure tests")
    class FailureTests {
        @Test
        @DisplayName("cancelOrder - order not found, should fail")
        void cancelOrder_orderNotFound_shouldFail() {
            lenient().when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(currentPersonService.getCurrentPersonId()).thenReturn(testUserId);
            when(orderRepository.findByIdAndUserId(testOrderDTO.id(), testUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.cancelOrder(testOrderDTO.id()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");

            verify(orderRepository, never()).save(testOrder);
        }

        @Test
        @DisplayName("cancelOrder - should fail, payment not found for current order")
        void cancelOrder_paymentNotFoundForOrder_should_fail() {
            lenient().when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(currentPersonService.getCurrentPersonId()).thenReturn(testUserId);
            when(orderRepository.findByIdAndUserId(testOrderDTO.id(), testUserId)).thenReturn(Optional.of(testOrder));
            when(paymentService.getPaymentByOrderId(testOrderDTO.id())).thenThrow(new ResourceNotFoundException("Payment not found"));

            assertThatThrownBy(() -> orderService.cancelOrder(testOrderDTO.id()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Payment not found");

            verify(orderRepository, never()).save(testOrder);
            verify(paymentService, never()).savePayment(testPayment);
        }

        @Test
        @DisplayName("cancelOrder - not valid order status, should fail")
        void cancelOrder_invalidOrderStatus_shouldFail() {
            testOrder.setStatus(OrderStatus.CANCELED);

            lenient().when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
            when(currentPersonService.getCurrentPersonId()).thenReturn(testUserId);
            when(orderRepository.findByIdAndUserId(testOrder.getId(), testUserId)).thenReturn(Optional.of(testOrder));
            when(paymentService.getPaymentByOrderId(testOrder.getId())).thenReturn(testPayment);

            assertThatThrownBy(() -> orderService.cancelOrder(testOrder.getId()))
                    .isInstanceOf(OrderCancellationException.class)
                    .hasMessageContaining("Order already canceled");

            verify(orderRepository, never()).save(testOrder);
            verify(paymentService, never()).savePayment(testPayment);
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
