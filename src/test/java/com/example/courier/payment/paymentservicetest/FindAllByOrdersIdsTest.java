package com.example.courier.payment.paymentservicetest;

import com.example.courier.domain.Payment;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindAllByOrdersIdsTest {

    @Mock
    PaymentRepository repository;

    @InjectMocks
    PaymentService service;

    @Test
    void successfullyFetchAndReturnPaymentList_whenRelatedOrderIdsIsGiven() {
        List<Long> orderIds = List.of(1L, 2L, 3L);
        List<Payment> payments = List.of(new Payment(), new Payment(), new Payment());

        when(repository.findAllByOrderIdIn(orderIds)).thenReturn(payments);

        var result = service.findAllByOrderIds(orderIds);

        assertEquals(payments, result);
        verify(repository).findAllByOrderIdIn(orderIds);
    }
}
