package com.example.courier.payment.method;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PaymentMethodRepository;
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
    private final CurrentPersonService currentPersonService;

    public PaymentMethodService(PaymentMethodRepository repository, CurrentPersonService currentPersonService) {
        this.repository = repository;
        this.currentPersonService = currentPersonService;
    }

    /**
     * Retrieves all saved payment methods for current user
     * and return it as DTOs
     *
     * @return a list of saved payment methods as DTOs
     * */
    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getSavedPaymentMethods() {
        Long userId = currentPersonService.getCurrentPersonId();
        List<PaymentMethod> paymentMethods = repository.findByUserIdAndSavedTrue(userId);

        return paymentMethods.stream()
                .map(this::covertToDTO)
                .toList();
    }

    /**
     * Retrieves a specific saved payment method by its ID
     *
     * @param id the payment method ID
     * @return an optional containing the DTO if found or empty if not
     */
    @Transactional(readOnly = true)
    public Optional<PaymentMethodDTO> getSavedPaymentMethod(Long id) {
        return repository.findById(id)
                .map(this::covertToDTO);
    }


    /**
     * Converts a payment method entity to its corresponding DTO representation
     *
     * @param paymentMethod they payment method entity
     * @return the DTO representation
     * @throws IllegalArgumentException if payment method type is not supported
     */
    private PaymentMethodDTO covertToDTO(PaymentMethod paymentMethod) {
        if (paymentMethod instanceof CreditCard card) {
            return new CreditCardDTO(
                    card.getId(),
                    card.getLast4(),
                    card.getExpiryDate(),
                    card.getCardHolderName(),
                    card.isSaved());
        }
        throw new IllegalArgumentException("Unknown payment method type.");
    }

    /**
     * Saves the given {@link PaymentMethod} entity
     *
     * @param paymentMethod the payment method to save
     */
    @Transactional
    private void savePaymentMethod(PaymentMethod paymentMethod) {
        repository.save(paymentMethod);
    }


    /**
     * Deactivates a saved payment method for the current user.
     *
     * Fetches {@link PaymentMethod} by ID and ensure it belongs to the currently authenticated user.
     * Calls the entity {@link PaymentMethod#softDelete()} method to perform type-specific
     * soft deletion.
     * Persists the changes.
     *
     * @param paymentMethodId the ID of the {@link PaymentMethod} to deactivate
     * @throws ResourceNotFoundException if the payment method is not found or does not belong to current user
     * */
    @Transactional
    public void deactivatePaymentMethodById(Long paymentMethodId) {
        Long currentUserId = currentPersonService.getCurrentPersonId();
        PaymentMethod paymentMethodToDeactivate = repository.findByIdAndUserIdAndSavedTrue(paymentMethodId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method was not found."));

        paymentMethodToDeactivate.softDelete();
        savePaymentMethod(paymentMethodToDeactivate);
        logger.info("Payment method with ID: {} was successfully deleted", paymentMethodId);
    }

    /**
     * Fetched a {@link PaymentMethod} by its ID
     *
     * @param paymentMethodId the ID of the payment method
     * @return the payment entity
     * @throws ResourceNotFoundException if no method is found using given ID
     * */
    public PaymentMethod fetchPaymentMethodById(Long paymentMethodId) {
        return repository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method was not found"));
    }

    /**
     * Persists the given {@link PaymentMethod}
     * @param paymentMethod the method to save
     */
    public void saveMethod(PaymentMethod paymentMethod) {
        repository.save(paymentMethod);
    }
}
