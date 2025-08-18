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
import com.example.courier.repository.PaymentAttemptRepository;
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

    /*
    * Process payment for given order
    * calls getPaymentByOrderIdAndUserId() method to fetch Payment by order ID
    * extract corresponding Order from the Payment
    * validates current payment status, throws if invalid
    * calls processPaymentHandler() method that delegate payment processing to appropriate handler
    * on success - update payment and order statuses and persist changes
    *
    * @param paymentRequestDTO the incoming payment request data
    * @param orderId the ID of the order to process payment for
    * @return a result of the payment processing response
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

    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderIdAndUserId(Long orderId) {
        Long currentUserId = currentPersonService.getCurrentPersonId();
        return paymentRepository.findByOrderIdAndUserId(orderId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id {} " + orderId));
    }

    public PaymentDetailsDTO getPaymentDetails(Long orderId) {
        Payment payment = getPaymentByOrderIdAndUserId(orderId);
        return PaymentDetailsDTO.fromPayment(payment);
    }

    public void paymentSectionUpdate(PaymentSectionUpdateRequest updateRequest) {
        PaymentStatus.isValidStatus(updateRequest.status());
        Payment payment = getPaymentById(updateRequest.id());
        paymentMapper.updatePaymentSectionFromRequest(updateRequest, payment);
        paymentRepository.save(payment);
    }

    public List<Payment> findAllByIds(List<Long> ids) {
        return paymentRepository.findAllByOrderIdIn(ids);
    }

    /*
    * Helper methods
    */

    /*
    * Finds supported handler and passes data to handle it
    */
    private PaymentResultResponse processPaymentHandler(PaymentRequestDTO paymentRequestDTO) {
        return paymentHandlers.stream()
                .filter(h -> h.isSupported(paymentRequestDTO))
                .findFirst()
                .orElseThrow(() -> new PaymentHandlerNotFoundException("No handler found"))
                .handle(paymentRequestDTO);
    }

    private Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment was not found"));
    }

    /*
    * Update payment, parcel and order statuses
    * and save payment
    * */
    private void handlePaymentSuccess(Payment payment) {

        payment.setStatus(PaymentStatus.PAID);
        payment.getOrder().setStatus(OrderStatus.CONFIRMED);
        payment.getOrder().getParcelDetails().setStatus(ParcelStatus.PICKING_UP);

        savePayment(payment);
        log.info("Payment succeeded for order id {} and payment id {}",
                payment.getOrder().getId(), payment.getId());
    }

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

    private Payment getValidatedPayment(Long orderId) {
        Payment payment = getPaymentByOrderIdAndUserId(orderId);
        validatePaymentState(payment);

        return payment;
    }
/*
    private PaymentAttempt createSaveAndReturnPaymentAttempt(Payment payment) {
        long randomN = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        String txId = Long.toString(randomN, 10).toUpperCase();

         PaymentAttempt paymentAttempt = new PaymentAttempt(
                 PaymentAttemptStatus.PENDING, ProviderType.UNKNOWN, "temp_" + txId);
         paymentAttempt.setPayment(payment);

         return paymentAttemptRepository.saveAndFlush(paymentAttempt);
    }*/
}