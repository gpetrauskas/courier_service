package com.example.courier.service.payment;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.dto.mapper.PaymentMapper;
import com.example.courier.dto.request.PaymentSectionUpdateRequest;
import com.example.courier.exception.*;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final List<PaymentHandler> paymentHandlers;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(List<PaymentHandler> paymentHandlers, PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper) {
        this.paymentHandlers = paymentHandlers;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public ResponseEntity<String> processPayment(PaymentRequestDTO paymentRequestDTO, Long orderId, Principal principal) {
        try {
            Payment payment = getPaymentByOrderId(orderId);
            Order order = payment.getOrder();

            if (!isPaymentValid(payment)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No valid payment founded.");
            }

            ResponseEntity<String> response = processPaymentHandler(paymentRequestDTO, payment);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                handlePaymentSuccess(payment, order);
            } else {
                throw new PaymentFailedException("Payment handler failed.");
            }

            return response;

        } catch (PaymentFailedException e) {
            log.error("Error occurred during payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during payment. " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during payment");
        }
    }

    private ResponseEntity<String> processPaymentHandler(PaymentRequestDTO paymentRequestDTO, Payment payment) {
        for (PaymentHandler handler : paymentHandlers) {
            if (handler.isSupported(paymentRequestDTO)) {
                ResponseEntity<String> response = handler.handle(paymentRequestDTO, payment);
                if (!response.getStatusCode().equals(HttpStatus.OK)) {
                    throw new PaymentFailedException(response.getBody());
                }
                return response;
            }
        }
        throw new PaymentFailedException("No handler for provided payment");
    }

    private void handlePaymentSuccess(Payment payment, Order order) {
        payment.setStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        order.getParcelDetails().setStatus(ParcelStatus.PICKING_UP);
        savePayment(payment);
        log.info("Payment succeeded for order id {} and payment id {}", order.getId(), payment.getId());
    }

    private boolean isPaymentValid(Payment payment) {
        if (payment.getStatus().equals(PaymentStatus.PAID) || payment.getStatus().equals(PaymentStatus.CANCELED)) {
            log.error("Payment not valid. Status: {}", payment.getStatus());
            return false;
        }
        log.info("Payment valid");
        return true;
    }

    public void createPayment(Order order, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.NOT_PAID);

        savePayment(payment);
        log.info("Payment id {} created for order with id {}", payment.getId(), order.getId());
    }

    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id {} " + orderId));
    }

    private Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment was not found"));
    }

    public PaymentDetailsDTO getPaymentDetails(Long orderId) {
        Payment payment = getPaymentByOrderId(orderId);
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
}