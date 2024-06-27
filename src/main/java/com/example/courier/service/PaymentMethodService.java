package com.example.courier.service;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<PaymentMethodDTO> getSavedPaymentMethod(Long id) {
        return Optional.of(paymentMethodRepository.findById(id)
                .map(this::covertToDTO).orElseGet(() -> (PaymentMethodDTO) ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error occurred getting payment method.")));
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
