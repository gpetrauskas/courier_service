package com.example.courier.service.order.command;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.order.factory.OrderFactory;
import com.example.courier.service.order.query.OrderQueryService;
import com.example.courier.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderCommandServiceImpl implements OrderCommandService {

    private static final Logger logger = LoggerFactory.getLogger(OrderCommandServiceImpl.class);
    private final OrderQueryService queryService;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final PaymentService paymentService;
    private final OrderFactory orderFactory;

    public OrderCommandServiceImpl(OrderQueryService queryService, OrderMapper mapper,
                                   OrderRepository repository, OrderFactory orderFactory,
                                   PaymentService paymentService) {
        this.queryService = queryService;
        this.mapper = mapper;
        this.repository = repository;
        this.orderFactory = orderFactory;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateOrderSection(OrderSectionUpdateRequest updateRequest) {
        Order order = queryService.fetchById(updateRequest.id());

        order.validateUpdatable(OrderStatus.valueOf(updateRequest.status()), updateRequest.deliveryPreferences());

        mapper.updateOrderSectionFromRequest(updateRequest, order);
        save(order);
    }

    @Override
    @Transactional
    public Map<String, String> placeOrder(OrderDTO orderDTO) {
        Order order = orderFactory.createNewOrderFromDTO(orderDTO);

        paymentService.createPayment(order);
        save(order);
        logger.info("Payment id {} created for order with id {}", order.getPayment().getId(), order.getId());

        return Map.of(
                "message", "Order was placed successfully. Order cost: " + order.getPayment().getAmount(),
                "cost", String.valueOf(order.getPayment().getAmount()),
                "orderId", String.valueOf(order.getId())
        );
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = queryService.getOrderByIdAndCurrentPersonId(orderId);

        order.cancel();
        save(order);
    }

    private void save(Order order) {
        repository.save(order);
    }
}
