package com.example.courier.service;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public void addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found."));

        if (paymentMethodDTO instanceof CreditCardDTO) {
            CreditCardDTO creditCardDTO = (CreditCardDTO) paymentMethodDTO;

            CreditCard card = new CreditCard();
            card.setUser(user);
            card.setCardNumber(creditCardDTO.cardNumber());
            card.setExpiryDate(creditCardDTO.expiryDate());
            card.setCardHolderName(creditCardDTO.cardHolderName());

            paymentMethodRepository.save(card);
        }
    }
}
