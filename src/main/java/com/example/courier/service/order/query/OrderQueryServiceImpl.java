    package com.example.courier.service.order.query;

    import com.example.courier.common.OrderStatus;
    import com.example.courier.common.ParcelStatus;
    import com.example.courier.common.PaymentStatus;
    import com.example.courier.domain.*;
    import com.example.courier.dto.OrderDTO;
    import com.example.courier.dto.PaginatedResponseDTO;
    import com.example.courier.dto.mapper.OrderMapper;
    import com.example.courier.dto.response.AdminOrderResponseDTO;
    import com.example.courier.exception.OrderCancellationException;
    import com.example.courier.exception.ResourceNotFoundException;
    import com.example.courier.repository.OrderRepository;
    import com.example.courier.service.payment.PaymentService;
    import com.example.courier.service.security.CurrentPersonService;
    import com.example.courier.specification.order.OrderSpecificationBuilder;
    import com.example.courier.util.PageableUtils;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.data.jpa.domain.Specification;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Map;

    @Service
    @Transactional(readOnly = true)
    public class OrderQueryServiceImpl implements OrderQueryService {

        private final OrderRepository orderRepository;
        private final OrderMapper mapper;
        private final PaymentService paymentService;
        private final CurrentPersonService currentPersonService;

        public OrderQueryServiceImpl(OrderRepository orderRepository, OrderMapper mapper, PaymentService paymentService,
                                     CurrentPersonService currentPersonService) {
            this.orderRepository = orderRepository;
            this.mapper = mapper;
            this.paymentService = paymentService;
            this.currentPersonService = currentPersonService;
        }

        /*
        * ADMIN
        */
        @Override
        public PaginatedResponseDTO<AdminOrderResponseDTO> getDetailedOrdersForAdmin(int page, int size, String orderStatus, Long id) {
            assertAdmin();
            Specification<Order> specification = OrderSpecificationBuilder.buildOrderSpecification(orderStatus, id);
            Page<Order> orderPage = fetchOrders(page, size, specification, "createDate", "asc");
            if (orderPage.isEmpty()) {
                return PageableUtils.empty();
            }

            Map<Long, Payment> paymentMap = fetchPayments(orderPage);
            List<AdminOrderResponseDTO> dtoList = mapOrdersWithPayments(orderPage, paymentMap);

            return PageableUtils.toPaginatedResponse(dtoList, orderPage);
        }

        @Override
        public PaginatedResponseDTO<OrderDTO> getOrdersForTaskAssignment(int page, int size, String taskType) {
            assertAdmin();
            Specification<Order> specification = OrderSpecificationBuilder.buildOrderSpecificationByTaskType(taskType);

            Page<Order> orderPage = fetchOrders(page, size, specification, "createDate", "asc");
            if (orderPage.isEmpty()) {
                return PageableUtils.empty();
            }

            return toPaginatedDTO(orderPage);
        }

        @Override
        public AdminOrderResponseDTO getAdminOrderById(Long id) {
            Order order = fetchById(id);
            Map<Long, Payment> paymentMap = paymentService.getPaymentsForOrders(List.of(order.getId()));

            return mapper.toAdminOrderResponseDTO(order, paymentMap.get(order.getId()));
        }

        @Override
        public List<Order> fetchAllByParcelDetails(List<Parcel> parcels) {
            assertAdmin();
            return orderRepository.findAllByParcelDetails(parcels);
        }

        @Override
        public Order fetchById(Long id) {
            return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order was not found"));
        }


        /*
        * USER
        */
        @Override
        public PaginatedResponseDTO<OrderDTO> findUserOrders(int page, int size) {
            Person person = currentPersonService.getCurrentPersonAs(User.class);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").ascending());

            Page<Order> orderPage = orderRepository.findByUserId(person.getId(), pageable);

            return toPaginatedDTO(orderPage);
        }

        @Override
        public OrderDTO findSelfOrderById(Long id) {
            return mapper.toOrderDTO(getOrderByIdAndCurrentPersonId(id));
        }


        /*
        * Inner
        */
        private Page<Order> fetchOrders(int page, int size, Specification<Order> specification, String sortBy, String sortDirection) {
            Pageable pageable = PageableUtils.createPageable(page, size, sortBy, sortDirection);
            return orderRepository.findAll(specification, pageable);
        }

        private Map<Long, Payment> fetchPayments(Page<Order> orderPage) {
            List<Long> orderIds = orderPage.stream().map(Order::getId).toList();
            return paymentService.getPaymentsForOrders(orderIds);
        }

        private List<AdminOrderResponseDTO> mapOrdersWithPayments(Page<Order> orderPage, Map<Long, Payment> paymentMap) {
            return orderPage.stream()
                    .map(o -> mapper.toAdminOrderResponseDTO(o, paymentMap.get(o.getId())))
                    .toList();
        }

        private void assertAdmin() {
            if (!currentPersonService.isAdmin()) {
                throw new AccessDeniedException("No access");
            }
        }

        private PaginatedResponseDTO<OrderDTO> toPaginatedDTO(Page<Order> orderPage) {
            Page<OrderDTO> dtoPage = orderPage.map(mapper::toOrderDTO);
            return PageableUtils.toPaginatedResponse(dtoPage.getContent(), dtoPage);
        }

        private Order getOrderByIdAndCurrentPersonId(Long orderId) {
            Long currentUserId = currentPersonService.getCurrentPersonId();
            return orderRepository.findByIdAndUserId(orderId, currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found or not owned by the user"));
        }
    }
