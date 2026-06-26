/*
package com.example.courier.payment.paymentservicetest;

import payment.domain.gytis.courier.PaymentStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.payment.PaymentService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatePaymentTests {

    @Mock
    private DeliveryMethodService deliveryMethodService;

    @InjectMocks
    private PaymentService service;

    private BigDecimal amountToPay;
    private Order mockOrder;

    @BeforeEach
    void setup() {
        amountToPay = BigDecimal.valueOf(20);
        mockOrder = new Order();
        mockOrder.setId(1L);
    }

    @Test
    void shouldSuccessfullyCreatePayment_whenAllGood() {
        BigDecimal expectedAmount = BigDecimal.valueOf(20);
        when(deliveryMethodService.calculateShippingCost(mockOrder)).thenReturn(expectedAmount);

        service.createPayment(mockOrder);

        Payment savedPayment = mockOrder.getPayment();

        assertEquals(mockOrder, savedPayment.getOrder());
        assertEquals(amountToPay, savedPayment.getAmount());
        assertEquals(PaymentStatus.NOT_PAID, savedPayment.getStatus());
    }
}
*/
