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

    public Map<Long, Payment> getPaymentsForOrders(List<Long> ordersId) {
        List<Payment> payments = paymentRepository.findAllByOrderIdIn(ordersId);

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
     * calls getValidatedPayment() method to fetch users Payment by order ID
     * Call PaymentAttemptService to create and return PaymentAttempt object
     * Calls processPaymentHandler() method that delegate payment processing to appropriate handler
     * On success: call PaymentAttemptService to update paymentAttempt
     * and call handlePaymentSuccess() method to update and save payment and order statuses
     * On failure: update paymentAttempt and rethrow exception
     *
     * @param paymentRequestDTO the incoming payment request data
     * @param orderId the ID of the order to process payment for
     * @return PaymentResultResponse a result of the payment processing response
     * @throws PaymentFailedException if the payment fails
     */
    @Transactional
    public PaymentResultResponse processPayment(PaymentRequestDTO paymentRequestDTO, Long orderId) {
        Payment payment = getValidatedPayment(orderId);
        PaymentAttempt paymentAttempt = attemptService.createAttempt(payment);

        try {
            PaymentResultResponse response = processPaymentHandler(paymentRequestDTO);

            attemptService.updateAttempt(paymentAttempt, PaymentAttemptStatus.SUCCESS, response.provider(), response.transactionId(), "");
            handlePaymentSuccess(payment);

            return response;
        } catch (PaymentFailedException e) {
            attemptService.updateAttempt(paymentAttempt, PaymentAttemptStatus.FAILED, e.getType(), "", e.getMessage());
            throw e;
        }
    }

    /** Creates and persists payment for the given order
     *
     * creates new payment entity for a specified order with the given amount
     * set initial status NOT_PAID and persists it to database
     *
     * @param order the Order entity for which payment is created
     * @param amount amount of the payment
     * @throws RuntimeException if creating payment fails
     */
    public void createPayment(Order order, BigDecimal amount) {
        try {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(amount);
            payment.setStatus(PaymentStatus.NOT_PAID);

            savePayment(payment);
            log.info("Payment id {} created for order with id {}", payment.getId(), order.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException("Payment creation failure: " + e.getMessage());
        }
    }

    /**
     * Persist given payment entity
     *
     * @param payment the payment to save
     */
    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    /**
    * Set the current user ID from the security context
    * fetch and a return payment entity by orderId and currentUserId
    *
    * @param orderId the ID of the order
    * @return payment the payment entity
    * @throws  ResourceNotFoundException - throws if payment is not found by orderId and currentUserId*/
    public Payment getPaymentByOrderIdAndUserId(Long orderId) {
        Long currentUserId = currentPersonService.getCurrentPersonId();
        return paymentRepository.findByOrderIdAndUserId(orderId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id {} " + orderId));
    }

    /**
     * Retrieves the payment details for the given orderId
     *
     * @param orderId the ID of the order
     * @return payment details as a DTO
     */
    public PaymentDetailsDTO getPaymentDetails(Long orderId) {
        Payment payment = getPaymentByOrderIdAndUserId(orderId);
        return PaymentDetailsDTO.fromPayment(payment);
    }

    /**
     * Updates payment section with provided data
     *
     * Validate the requested status
     * fetch payment by ID and apply updates from the DTO by using paymentMapper
     * and persists the changes
     *
     * @param updateRequest  the incoming payment update data
     * @throws IllegalArgumentException if the status is invalid
     */
    public void paymentSectionUpdate(PaymentSectionUpdateRequest updateRequest) {
        PaymentStatus.isValidStatus(updateRequest.status());
        Payment payment = getPaymentById(updateRequest.id());
        paymentMapper.updatePaymentSectionFromRequest(updateRequest, payment);
        paymentRepository.save(payment);
    }

    /**
     * Retrieves a list of payments by list of IDS
     *
     * @param ids the list of payment ids to be fetched
     * @return payments the list of fetched payment entities
     */
    public List<Payment> findAllByIds(List<Long> ids) {
        return paymentRepository.findAllByOrderIdIn(ids);
    }

    /*
    * Helper methods
    */

    /**
     * Delegates the payment request to the first supported handler
     * Iterates through the available implementations to find one that supports the given paymentRequestDTO
     * once founded it delegates the handling of request to that handler.
     * If no supported handler found an exception is thrown
     *
     * @param paymentRequestDTO payment request to be processed
     * @return the result of processed payment
     * @throws PaymentHandlerNotFoundException if no handler supports the given request
     */
    private PaymentResultResponse processPaymentHandler(PaymentRequestDTO paymentRequestDTO) {
        return paymentHandlers.stream()
                .filter(h -> h.isSupported(paymentRequestDTO))
                .findFirst()
                .orElseThrow(() -> new PaymentHandlerNotFoundException("No handler found"))
                .handle(paymentRequestDTO);
    }

    /**
     * Find a payment by its ID or throw an exception if not found
     *
     * @param id of the payment to retrieve
     * @return payment entity
     * @throws ResourceNotFoundException throws if payment not found
     */
    private Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment was not found"));
    }

    /**
     * Handles a successful payment by updating the payment, order and parcel statuses,
     * and then persists the updated payment entity
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
     * Validates the payment state
     * ensures that the payment has not been already paid or canceled
     * and that related order and parcel details are present
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
     * Finds and validates a payment associated with given orderId
     * Ensures that the payment is in valid state before returning it
     *
     * @param orderId the ID of the order linked to the payment
     * @return payment the validated entity
     * @throws ValidationException if the payment status is invalid
     * @throws IllegalStateException if the order or parcel details are missing
     */
    private Payment getValidatedPayment(Long orderId) {
        Payment payment = getPaymentByOrderIdAndUserId(orderId);
        validatePaymentState(payment);

        return payment;
    }
}