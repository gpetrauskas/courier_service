package com.example.courier.payment;

import com.example.courier.repository.PaymentAttemptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentAttemptServiceTest {

    @Mock
    PaymentAttemptRepository repository;

    @InjectMocks
    PaymentAttemptService service;

    @Test
    void successfullyCreateInitialPaymentAttempt() {
        
    }
}
