/*
package com.example.courier.payment.paymentservicetest;

import com.example.courier.domain.Payment;
import exception.gytis.courier.ResourceNotFoundException;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.adapter.in.security.CurrentPersonUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetPaymentByOrderIdAndUserJpaEntityIdTest {

    @Mock
    PaymentRepository repository;
    @Mock
    CurrentPersonUseCase currentPersonService;

    @InjectMocks
    PaymentService service;

    @Test
    void returnsPayment_whenFoundForCurrentUser() {
        Long userId = 1L;
        Long orderId = 2L;
        Payment payment = new Payment();

        when(currentPersonService.getCurrentPersonId()).thenReturn(userId);
        when(repository.findByOrderIdAndUserId(orderId, userId)).thenReturn(Optional.of(payment));

        var response = service.getPaymentByOrderIdAndUserId(orderId);

        assertEquals(response, payment);
        verify(repository).findByOrderIdAndUserId(orderId, userId);
    }

    @Test
    void shouldThrow_whenPaymentNotFound() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(repository.findByOrderIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getPaymentByOrderIdAndUserId(2L));
    }

}
*/
