package com.example.courier.service.order.query;

import com.example.courier.domain.*;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.specification.order.OrderSpecificationBuilder;
import com.example.courier.util.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

/**
 * Read only implementation of {@link OrderQueryService}.
 * */
@Service
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final CurrentPersonService currentPersonService;

    public OrderQueryServiceImpl(OrderRepository orderRepository, OrderMapper mapper,
                                 CurrentPersonService currentPersonService) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.currentPersonService = currentPersonService;
    }

    /*
    * ADMIN
    */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public PaginatedResponseDTO<AdminOrderResponseDTO> getDetailedOrdersForAdmin(int page, int size, String orderStatus, Long id) {
        Specification<Order> specification = OrderSpecificationBuilder.buildOrderSpecification(orderStatus, id);

        return fetchAndMap(specification, "createDate", "asc", page, size, mapper::toAdminOrderResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public PaginatedResponseDTO<OrderDTO> getOrdersForTaskAssignment(int page, int size, String taskType) {
        Specification<Order> specification = OrderSpecificationBuilder.buildOrderSpecificationByTaskType(taskType);

        return fetchAndMap(specification, "createDate", "asc", page, size, mapper::toOrderDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public AdminOrderResponseDTO getAdminOrderById(Long id) {
        Order order = fetchById(id);

        return mapper.toAdminOrderResponseDTO(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<Order> fetchAllByParcelDetails(List<Parcel> parcels) {
        return orderRepository.findAllByParcelDetails(parcels);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Order fetchById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Order with id " + id + " was not found")
        );
    }

    /*
    * USER
    */
    @Override
    public PaginatedResponseDTO<OrderDTO> findUserOrders(int page, int size) {
        Long currentUserId = currentPersonService.getCurrentPersonId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").ascending());

        Page<Order> orderPage = orderRepository.findByUserId(currentUserId, pageable);

        return toPaginatedDTO(orderPage);
    }

    @Override
    public OrderDTO findSelfOrderById(Long id) {
        return mapper.toOrderDTO(getOrderByIdAndCurrentPersonId(id));
    }

    /*
    * Inner
    */

    private <T> PaginatedResponseDTO<T> fetchAndMap(Specification<Order> spec, String sortBy, String sortDirection,
                                                    int page, int size, Function<Order, T> mapperFunction) {
        Page<Order> orderPage = fetchOrders(page, size, spec, sortBy, sortDirection);
        if (orderPage.isEmpty()) {
            return PageableUtils.empty();
        }

        List<T> dtoList = orderPage.stream().map(mapperFunction).toList();
        return PageableUtils.toPaginatedResponse(dtoList, orderPage);
    }

    private Page<Order> fetchOrders(int page, int size, Specification<Order> specification, String sortBy, String sortDirection) {
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, sortDirection);
        return orderRepository.findAll(specification, pageable);
    }

    private PaginatedResponseDTO<OrderDTO> toPaginatedDTO(Page<Order> orderPage) {
        Page<OrderDTO> dtoPage = orderPage.map(mapper::toOrderDTO);
        return PageableUtils.toPaginatedResponse(dtoPage.getContent(), dtoPage);
    }

    public Order getOrderByIdAndCurrentPersonId(Long orderId) {
        Long currentUserId = currentPersonService.getCurrentPersonId();
        return orderRepository.findByIdAndUserId(orderId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or not owned by the user"));
    }
}
