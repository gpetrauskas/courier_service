package com.example.courier;


import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.domain.User;
import com.example.courier.dto.*;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private List<PaymentHandler> paymentHandlers;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentHandler paymentHandler;

    @Mock
    private Principal principal;

    private PaymentRequestDTO paymentRequestDTO;
    private Payment payment;
    private Order order;
    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("myemail@email.com");
        user.setName("Vardas Pavarde");

        order = new Order();
        order.setUser(user);

        payment = new Payment();
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setOrder(order);

        AddressDTO senderAddress = new AddressDTO(1L, "klaipeda", "sezamu", "13",
                "1", "123123123", "321", "vardas");
        AddressDTO recipientAddress = new AddressDTO(2L, "klaipeda", "java", "23",
                "3", "555123123", "421", "pavarde");
        ParcelDTO parcelDTO = new ParcelDTO(99L, "2kg", "30x30x30", "knygos",
                "123456", ParcelStatus.WAITING_FOR_PAYMENT);
        OrderDTO orderDTO = new OrderDTO(88L, senderAddress, recipientAddress, parcelDTO,
                "express", OrderStatus.PENDING, LocalDateTime.now());

        CreditCardDTO paymentMethodDTO = new CreditCardDTO(66L, "1111111111119",
                "12/30", "Vardas Pavarde", "121", false);
        paymentRequestDTO = new PaymentRequestDTO(orderDTO, 77L, paymentMethodDTO, new BigDecimal("100"),
                PaymentStatus.NOT_PAID, "123");
    }

    @Test
    public void toProcessPayment_Success() {
        when(paymentRepository.findByOrderId(any(Long.class))).thenReturn(Optional.of(payment));
        when(paymentHandlers.stream()).thenReturn(List.of(paymentHandler).stream());
        when(paymentHandler.isSupported(any(PaymentRequestDTO.class))).thenReturn(true);
        when(paymentHandler.handle(any(PaymentRequestDTO.class), any(Payment.class)))
                .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));
        when(principal.getName()).thenReturn("myemail@email.com");

        ResponseEntity<String> response = paymentService.processPayment(paymentRequestDTO, order.getId(), principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());

        verify(paymentRepository, times(1)).save(payment);


    }


}
