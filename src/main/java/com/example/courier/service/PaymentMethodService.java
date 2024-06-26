package com.example.courier.service;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CreditCardService creditCardService;

    public void addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found."));

        if (paymentMethodDTO instanceof CreditCardDTO) {
            CreditCard card = creditCardService.setupCreditCard((CreditCardDTO) paymentMethodDTO, user);
            paymentMethodRepository.save(card);
        }
    }

    public List<PaymentMethodDTO> getSavedPaymentMethods(Long userId) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(userId);

        return paymentMethods.stream()
                .map(this::covertToDTO)
                .toList();
    }

    private PaymentMethodDTO covertToDTO(PaymentMethod paymentMethod) {
        if (paymentMethod instanceof CreditCard) {
            CreditCard card = (CreditCard) paymentMethod;
            return new CreditCardDTO(
                    card.getId(),
                    card.maskCardNumber(),
                    card.getExpiryDate(),
                    card.getCardHolderName(),
                    "",
                    card.isSaved());
        }

        return null;

    }
}
