package com.example.courier;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private AuthService authService;
    @Mock
    private AddressService addressService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private PricingOptionService pricingOptionService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;

    private User user;
    private OrderDTO orderDTO;
    private Order order;
    private Parcel aParcel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
       // user.setId(1L);
        user.setEmail("myemail@mail.com");

        AddressDTO senderAddress = new AddressDTO(1L, "klaipeda", "sezamu", "13", "1", "123123123", "321", "vardas");
        AddressDTO recipientAddress = new AddressDTO(2L, "klaipeda", "javos", "23", "3", "55123321", "421", "bevardis");
        ParcelDTO parcelDTO = new ParcelDTO(99L, "2kg", "30x30x30", "knygos", "12312312151555", ParcelStatus.PICKING_UP);
        orderDTO = new OrderDTO(88L, senderAddress, recipientAddress, parcelDTO, "express", OrderStatus.PENDING, LocalDateTime.now());

        order = new Order();
        order.setUser(user);
        order.setSenderAddress(new OrderAddress());
        order.setRecipientAddress(new OrderAddress());
        order.setParcelDetails(new Parcel());
        order.setCreateDate(LocalDateTime.now());
    }

    @Test
    public void testPlaceOrder_Success() {
        when(authService.getUserById(1L)).thenReturn(user);
        when(addressService.fetchOrCreateOrderAddress(any(AddressDTO.class), eq(user)))
                .thenReturn(new OrderAddress());
        when(orderMapper.toOrder(orderDTO)).thenReturn(order);
        when(pricingOptionService.calculateShippingCost(orderDTO)).thenReturn(new BigDecimal("19"));

        orderService.placeOrder(1L, orderDTO);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(paymentService, times(1)).createPayment(any(Order.class), any(BigDecimal.class));
    }


}
