package com.example.courier.payment;

import com.example.courier.common.*;
import com.example.courier.domain.*;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.dto.mapper.PaymentMapper;
import com.example.courier.dto.request.order.PaymentSectionUpdateRequest;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.*;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.service.security.CurrentPersonService;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing payments, including creation, updates and processing
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final List<PaymentHandler> paymentHandlers;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CurrentPersonService currentPersonService;
    private final PaymentAttemptService attemptService;

    public PaymentService(List<PaymentHandler> paymentHandlers, PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper, CurrentPersonService currentPersonService,
                          PaymentAttemptService attemptService) {
        this.paymentHandlers = paymentHandlers;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.currentPersonService = currentPersonService;
        this.attemptService = attemptService;
    }

    /**
     * Retrieve all payments associated with  the given list of orderIds
     *
     * Calls repository for the payments matching the provided order ids.
     * If no payment found, a {@link ResourceNotFoundException} is thrown.
     * On success, the result is returned as a Map where:
     * Key = order Id,
     * value = corresponding {@link Payment}
     *
     * @param ordersIds the list of the order ids to fetch payments for
     * @return a map of order Ids to their corresponding payments
     * @throws ResourceNotFoundException if no payment is found for the given order Ids
     * */
    public Map<Long, Payment> getPaymentsForOrders(List<Long> ordersIds) {
        List<Payment> payments = paymentRepository.findAllByOrderIdIn(ordersIds);

        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for the given order");
        }
        return payments.stream()
                .collect(Collectors.toMap(
                        payment -> payment.getOrder().getId(),
                        payment -> payment
                ));
    }

    /**
     * Processes payment for the given order
     *
     * Validates and fetches the payment for the order
     * Creates a {@link PaymentAttempt} object
     * Delegate to appropriate {@link PaymentHandler} for processing
     * On success: update attempt status and order/payment state.
     * On failure: update payment attempt and rethrow exception
     *
     * @param paymentRequestDTO the incoming {@link PaymentRequestDTO} data
     * @param orderId the ID of the order to process payment for
     * @return {@link PaymentResultResponse} a result of the payment processing response
     * @throws PaymentFailedException if the payment fails
     */
    @Transactional
    public PaymentResultResponse processPayment(PaymentRequestDTO paymentRequestDTO, Long orderId) {
        Payment payment = getValidatedPayment(orderId);
        PaymentAttempt paymentAttempt = attemptService.createAttempt(payment);

        try {
            PaymentResultResponse response = processPaymentHandler(paymentRequestDTO, payment.getOrder().getUser(), payment.getAmount());

            attemptService.updateAttempt(paymentAttempt, PaymentAttemptStatus.SUCCESS, response.provider(), response.transactionId(), "");
            handlePaymentSuccess(payment);

            return response;
        } catch (PaymentFailedException e) {
            attemptService.updateAttempt(paymentAttempt, PaymentAttemptStatus.FAILED, e.getType(), "", e.getMessage());
            throw e;
        }
    }

    /** Creates and persists {@link Payment} for the given {@link Order}
     *
     * creates new payment entity for a specified order with the given amount
     * set initial status {@code NOT_PAID} and persists it to database
     *
     * @param order the {@link Order} entity for which payment is created
     * @param amount amount of the payment
     * @throws PaymentCreationException if creating payment fails
     */
    public void createPayment(Order order, BigDecimal amount) {
        try {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(amount);
            payment.setStatus(PaymentStatus.NOT_PAID);

            savePayment(payment);
            log.info("Payment id {} created for order with id {}", payment.getId(), order.getId());
        } catch (PaymentCreationException e) {
            throw new PaymentCreationException("Payment creation failure: " + e.getMessage());
        }
    }

    /**
     * Persist given {@link Payment} entity
     *
     * @param payment the {@link Payment} to save
     */
    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    /**
     * Set the current user ID from the security context
     * fetch and a return {@link Payment} entity by orderId and currentUserId
     *
     * @param orderId the ID of the order
     * @return payment the {@link Payment} entity
     * @throws ResourceNotFoundException throws if payment is not found by orderId and currentUserId
     */
    public Payment getPaymentByOrderIdAndUserId(Long orderId) {
        Long currentUserId = currentPersonService.getCurrentPersonId();
        return paymentRepository.findByOrderIdAndUserId(orderId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id {} " + orderId));
    }

    /**
     * Retrieves the payment details for the given orderId
     *
     * @param orderId the ID of the order
     * @return payment details as a {@link PaymentDetailsDTO}
     */
    public PaymentDetailsDTO getPaymentDetails(Long orderId) {
        Payment payment = getPaymentByOrderIdAndUserId(orderId);
        return PaymentDetailsDTO.fromPayment(payment);
    }

    /**
     * Updates payment section with provided data
     *
     * Validate the requested status
     * fetch {@link Payment} by ID and apply updates from the {@link PaymentSectionUpdateRequest}
     * by using {@link PaymentMapper} and persists the changes
     *
     * @param updateRequest  the incoming {@link PaymentSectionUpdateRequest} data
     * @throws IllegalArgumentException if the status is invalid
     */
    public void paymentSectionUpdate(PaymentSectionUpdateRequest updateRequest) {
        PaymentStatus.isValidStatus(updateRequest.status());
        Payment payment = getPaymentById(updateRequest.id());
        paymentMapper.updatePaymentSectionFromRequest(updateRequest, payment);
        paymentRepository.save(payment);
    }

    /**
     * Retrieves a list of {@link Payment} with a given list of order IDS
     *
     * @param orderIds the list of order ids
     * @return payments the list of fetched {@link Payment} entities
     */
    public List<Payment> findAllByOrderIds(List<Long> orderIds) {
        return paymentRepository.findAllByOrderIdIn(orderIds);
    }

    /*
    * Helper methods
    */

    /**
     * Delegates the {@link PaymentRequestDTO} to the first supported {@link PaymentHandler}
     * Iterates through the available implementations to find one that supports the given paymentRequestDTO
     * once found, it delegates the handling of request to that handler.
     * If no supported handler found an exception is thrown
     *
     * @param paymentRequestDTO the {@link PaymentRequestDTO} to be processed
     * @return {@link PaymentResultResponse} the result of processed payment
     * @throws PaymentHandlerNotFoundException if no handler supports the given request
     */
    private PaymentResultResponse processPaymentHandler(PaymentRequestDTO paymentRequestDTO, User user, BigDecimal amount) {
        return paymentHandlers.stream()
                .filter(h -> h.isSupported(paymentRequestDTO))
                .findFirst()
                .orElseThrow(() -> new PaymentHandlerNotFoundException("No handler found"))
                .handle(paymentRequestDTO, user, amount);
    }

    /**
     * Find a payment by its ID or throw an exception if not found
     *
     * @param id of the {@link Payment} to retrieve
     * @return payment entity
     * @throws ResourceNotFoundException throws if payment not found
     */
    private Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment was not found"));
    }

    /**
     * Handles a successful payment by updating the {@link Payment},
     * {@link Order} and {@link Parcel} statuses.
     * Persists the updated {@link Payment} entity
     *
     * @param payment entity to process
     */
    private void handlePaymentSuccess(Payment payment) {

        payment.setStatus(PaymentStatus.PAID);
        payment.getOrder().setStatus(OrderStatus.CONFIRMED);
        payment.getOrder().getParcelDetails().setStatus(ParcelStatus.PICKING_UP);

        savePayment(payment);
        log.info("Payment succeeded for order id {} and payment id {}",
                payment.getOrder().getId(), payment.getId());
    }

    /**
     * Validates the {@link Payment} state
     * ensures that the payment has not been already paid or canceled
     * and that related {@link Order} and {@link Parcel} details are present
     *
     * @param payment entity to validate
     * @throws ValidationException if the payment status is not NOT_PAID
     * @throws IllegalStateException if the order or parcel details is missing
     * */
    private void validatePaymentState(Payment payment) {
        if (payment.getStatus() != PaymentStatus.NOT_PAID) {
            log.error("Payment not valid. Status: {}", payment.getStatus());
            throw new ValidationException("Payment is already processed or canceled.");
        }

        if (payment.getOrder() == null || payment.getOrder().getParcelDetails() == null) {
            throw new IllegalStateException("Payment data missing");
        }
        log.info("Payment valid");
    }

    /**
     * Finds and validates a {@link Payment} associated with given orderId
     * Ensures that the payment is in valid state before returning it
     *
     * @param orderId the ID of the order linked to the payment
     * @return payment the validated {@link Payment} entity
     * @throws ValidationException if the payment status is invalid
     * @throws IllegalStateException if the order or parcel details are missing
     */
    private Payment getValidatedPayment(Long orderId) {
        Payment payment = getPaymentByOrderIdAndUserId(orderId);
        validatePaymentState(payment);

        return payment;
    }
}