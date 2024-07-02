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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    @Autowired
    private UserService userService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CreditCardService creditCardService;

    @Transactional
    public void addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = userService.getUserById(userId);

        PaymentMethod paymentMethod = createPaymentMethod(paymentMethodDTO, user);
        savePaymentMethod(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getSavedPaymentMethods(Long userId) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(userId);

        return paymentMethods.stream()
                .map(this::covertToDTO)
                .toList();
    }

    private PaymentMethod createPaymentMethod(PaymentMethodDTO paymentMethodDTO, User user) {
        if (paymentMethodDTO instanceof CreditCardDTO) {
            return creditCardService.setupCreditCard((CreditCardDTO) paymentMethodDTO, user);
        }
        throw new IllegalArgumentException("Not known method type");
    }

    @Transactional(readOnly = true)
    public Optional<PaymentMethodDTO> getSavedPaymentMethod(Long id) {
        return paymentMethodRepository.findById(id)
                .map(this::covertToDTO);
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
        throw new IllegalArgumentException("Unknown payment method type.");
    }

    @Transactional
    private void savePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodRepository.save(paymentMethod);
    }
}
