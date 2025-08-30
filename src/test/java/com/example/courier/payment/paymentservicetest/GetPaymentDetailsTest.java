package com.example.courier.payment.paymentservicetest;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Payment;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetPaymentDetailsTest {

    @Mock
    PaymentRepository repository;
    @Mock
    CurrentPersonService currentPersonService;

    @InjectMocks
    PaymentService service;

    @Test
    void successfullyReturnsPaymentDetailsAsDto_whenPaymentFoundAndConverted() {
        Long userId = 1L;
        Long orderId = 2L;

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setAmount(BigDecimal.valueOf(20));
        PaymentDetailsDTO detailsDTO = new PaymentDetailsDTO(BigDecimal.valueOf(20), "NOT_PAID");

        when(currentPersonService.getCurrentPersonId()).thenReturn(userId);
        when(repository.findByOrderIdAndUserId(orderId, userId)).thenReturn(Optional.of(payment));

        var response = service.getPaymentDetails(orderId);

        assertEquals(detailsDTO, response);
    }


}
