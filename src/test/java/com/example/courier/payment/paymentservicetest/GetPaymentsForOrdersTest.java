package com.example.courier.payment.paymentservicetest;

import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetPaymentsForOrdersTest {

    @Mock
    PaymentRepository repository;

    @InjectMocks
    PaymentService paymentService;

    private final Long ORDER_ID = 1L;

    @Test
    void shouldReturnPaymentsMap_whenPaymentsExists() {
        Order mockOrder = new Order();
        mockOrder.setId(1L);

        Payment mockPayment = new Payment();
        mockPayment.setOrder(mockOrder);

        when(repository.findAllByOrderIdIn(List.of(ORDER_ID))).thenReturn(List.of(mockPayment));

        var response = paymentService.getPaymentsForOrders(List.of(ORDER_ID));

        assertThat(response).containsEntry(ORDER_ID, mockPayment);
        verify(repository).findAllByOrderIdIn(any());
    }

    @Test
    void shouldThrowException_whenPaymentsListIsEmpty() {
        when(repository.findAllByOrderIdIn(List.of(ORDER_ID))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentsForOrders(List.of(ORDER_ID)));
    }
}
