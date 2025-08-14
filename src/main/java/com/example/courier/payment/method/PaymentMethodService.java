package com.example.courier.service.payment;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodService.class);
    private final PaymentMethodRepository repository;
    private final CreditCardService creditCardService;
    private final PersonService personService;
    private final CurrentPersonService currentPersonService;

    public PaymentMethodService(PaymentMethodRepository repository, CreditCardService creditCardService,
                                PersonService personService, CurrentPersonService currentPersonService) {
        this.repository = repository;
        this.creditCardService = creditCardService;
        this.personService = personService;
        this.currentPersonService = currentPersonService;
    }

    @Transactional
    public void addPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        Long userId = currentPersonService.getCurrentPersonId();
        User user = personService.fetchPersonByIdAndType(userId, User.class);

        PaymentMethod paymentMethod = createPaymentMethod(paymentMethodDTO, user);
        savePaymentMethod(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getSavedPaymentMethods(Long userId) {
        List<PaymentMethod> paymentMethods = repository.findByUserIdAndSavedTrue(userId);

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
        return repository.findById(id)
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
        repository.save(paymentMethod);
    }

    @Transactional
    public void deactivatePaymentMethodById(User user, Long paymentMethodId) {
        PaymentMethod paymentMethodToDeactivate = repository.findByIdAndSavedTrue(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment method was not found."));

        if (!paymentMethodToDeactivate.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to delete this method");
        }

        paymentMethodToDeactivate.setSaved(false);
        savePaymentMethod(paymentMethodToDeactivate);

        logger.info("Payment method with ID: {} was successfully deleted", paymentMethodId);
    }

    public PaymentMethod fetchPaymentMethodById(Long paymentMethodId) {
        return repository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method was not found"));
    }
}
