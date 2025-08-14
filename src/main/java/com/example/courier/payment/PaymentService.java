package com.example.courier.payment.method;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.dto.mapper.PaymentMapper;
import com.example.courier.dto.request.order.PaymentSectionUpdateRequest;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.*;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
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

    public PaymentService(List<PaymentHandler> paymentHandlers, PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper) {
        this.paymentHandlers = paymentHandlers;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
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

    @Transactional
    public PaymentResultResponse processPayment(PaymentRequestDTO paymentRequestDTO, Long orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        Order order = payment.getOrder();

        if (!isPaymentValid(payment)) {
            return new PaymentResultResponse("failure", "No valid payment founded.");
        }

        PaymentResultResponse response = processPaymentHandler(paymentRequestDTO, payment);
        if (!response.status().equals("success")) {
            throw new IllegalArgumentException(response.message());
        }
        handlePaymentSuccess(payment, order);
        return response;
    }

    private PaymentResultResponse processPaymentHandler(PaymentRequestDTO paymentRequestDTO, Payment payment) {
        return paymentHandlers.stream()
                .filter(h -> h.isSupported(paymentRequestDTO))
                .findFirst()
                .orElseThrow(() -> new PaymentFailedException("No handler found"))
                .handle(paymentRequestDTO);
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