package com.example.courier.payment.paymentservicetest;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Payment;
import com.example.courier.dto.mapper.PaymentMapper;
import com.example.courier.dto.request.order.PaymentSectionUpdateRequest;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.PaymentRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentSectionUpdateTest {

    @Mock
    PaymentRepository repository;
    @Mock
    PaymentMapper mapper;

    @InjectMocks
    PaymentService service;

    @ParameterizedTest(name = "input = ''{0}''")
    @NullSource
    @ValueSource(strings = {"wrong_status", "", " "})
    void shouldThrow_whenPaymentStatusNotExistsOrIsNullOrEmpty(String input) {
        PaymentSectionUpdateRequest request = new PaymentSectionUpdateRequest(1L, "payment", input);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.paymentSectionUpdate(request));

        assertEquals("Invalid payment status", ex.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class)
    void shouldSuccessfullyUpdatePaymentSection(PaymentStatus status) {
        Long paymentId = 2L;
        Payment payment = new Payment();
        PaymentSectionUpdateRequest request = new PaymentSectionUpdateRequest(paymentId, "payment", status.name());

        when(repository.findById(2L)).thenReturn(Optional.of(payment));
        doAnswer(invocationOnMock -> {
            PaymentSectionUpdateRequest request1 = invocationOnMock.getArgument(0);
            Payment p = invocationOnMock.getArgument(1);
            p.setStatus(PaymentStatus.valueOf(request1.status()));
            return null;
        }).when(mapper).updatePaymentSectionFromRequest(request, payment);

        service.paymentSectionUpdate(request);

        assertEquals(status, payment.getStatus());
        verify(mapper).updatePaymentSectionFromRequest(request, payment);
        verify(repository).save(payment);
    }
}
