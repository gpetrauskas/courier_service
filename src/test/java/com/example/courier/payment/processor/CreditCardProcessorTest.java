package com.example.courier.payment.processor;

import com.example.courier.common.ProviderType;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PayPal;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.method.CreditCardService;
import com.example.courier.service.permission.PermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditCardProcessorTest {

    @Mock
    CreditCardService creditCardService;
    @Mock
    PermissionService permissionService;

    @InjectMocks
    CreditCardProcessor creditCardProcessor;

    @Test
    void support_successfullyPass_whenCreditCardMethodIsGiven() {
        PaymentMethod cc = new CreditCard();

        boolean supported = creditCardProcessor.supports(cc);

        assertTrue(supported);
    }

    @Test
    void support_shouldReturnFalse_whenMethodGivenIsNotCreditCard() {
        PaymentMethod notCC = new PayPal();

        boolean supported = creditCardProcessor.supports(notCC);

        assertFalse(supported);
    }

    @Test
    void processShouldSuccessfullyPassAndReturnResponse_whenRequestIsValid() {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(null, mock(CreditCardDTO.class), "321");
        BigDecimal amount = new BigDecimal(20);

        CreditCard cc = new CreditCard();
        cc.setSaved(true);
        cc.setCardHolderName("Bilbo Baggins");
        User user = new User("Bilbo Baggins", "bilbo@middleearth.com", "validPass123");
        cc.setUser(user);

        PaymentResultResponse expectedResponse = new PaymentResultResponse("success", "APPROVED", ProviderType.CREDIT_CARD, "txId_123");

        when(permissionService.hasPaymentMethodAccess(cc)).thenReturn(true);
        when(creditCardService.chargeSavedCard(cc, requestDTO.cvc(), amount)).thenReturn(expectedResponse);

        var response = creditCardProcessor.process(cc, requestDTO, amount);

        assertEquals(expectedResponse, response);
    }
}
