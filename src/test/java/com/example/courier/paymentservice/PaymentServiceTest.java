package com.example.courier.paymentservice;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.mapper.PaymentMapper;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock List<PaymentHandler> paymentHandler;
    @Mock PaymentRepository repository;
    @Mock PaymentMapper mapper;

    @InjectMocks PaymentService paymentService;

    private PaymentRequestDTO requestDTO;
    private final Long ORDER_ID = 1L;

    @BeforeEach
    void setup() {
        PaymentMethodDTO paymentMethodDTO = new CreditCardDTO(77L, "123456789", "2026/07", "Gytis Petrauskas", "123", false);
        OrderDTO orderDTO = new OrderDTO(1L, null, null, null, null, null, null);
        requestDTO = new PaymentRequestDTO(orderDTO, 99L, paymentMethodDTO, BigDecimal.valueOf(20), PaymentStatus.NOT_PAID, "");
    }

    @Test
    void shouldThrow_whenPaymentNotFoundByOrderId() {
        when(repository.findByOrderId(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.processPayment(requestDTO, ORDER_ID));
    }

    @Test
    void shouldThrow_whenPaymentIsNotValidByTheStatus() {
        Payment payment = createMockPayment();
        payment.setStatus(PaymentStatus.CANCELED);

        when(repository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(payment));

        var response = paymentService.processPayment(requestDTO, ORDER_ID);

        assertEquals("No valid payment founded.", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Payment createMockPayment() {
        Order order = new Order();
        order.setId(1L);

        Payment payment = new Payment();
        payment.setId(2L);

        payment.setOrder(order);

        return payment;
    }
}
