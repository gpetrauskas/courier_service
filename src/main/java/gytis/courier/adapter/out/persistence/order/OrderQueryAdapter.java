package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.adapter.out.persistence.order.projection.*;
import gytis.courier.adapter.out.persistence.payment.PaymentJpaRepository;
import gytis.courier.adapter.out.persistence.task.OrderAddressIdsProjection;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.order.OrderQueryPort;
import gytis.courier.application.query.filter.OrderQuery;
import gytis.courier.application.readmodel.order.AdminOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderUserDetailReadModel;
import gytis.courier.application.readmodel.order.UserOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderAdminDetailReadModel;
import gytis.courier.application.readmodel.order.OrderForTaskReadModel;
import gytis.courier.domain.address.AddressType;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskItemCreationSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class OrderQueryAdapter implements OrderQueryPort {
    private final OrderJpaRepository repository;
    private final PaymentJpaRepository paymentJpaRepository;
    private final OrderReadModelMapper readModelMapper;
    private final OrderSnapshotMapper snapshotMapper;

    public OrderQueryAdapter(OrderJpaRepository repository, PaymentJpaRepository paymentJpaRepository, OrderReadModelMapper readModelMapper, OrderSnapshotMapper snapshotMapper) {
        this.repository = repository;
        this.paymentJpaRepository = paymentJpaRepository;
        this.readModelMapper = readModelMapper;
        this.snapshotMapper = snapshotMapper;
    }

    @Override
    public PageResult<OrderForTaskReadModel> findAllForTask(PageQuery pageQuery, Set<ParcelStatus> statuses, AddressType type) {
        Pageable pageable = PageableFactory.from(pageQuery);

        Page<OrderForTaskProjection> orders = (type == AddressType.senderAddress)
                ? repository.findAllForPickup(statuses, pageable)
                : repository.findAllForDelivery(statuses, pageable);

        return PageResultMapper.from(
                orders,
                readModelMapper::toReadModel
        );
    }

    @Override
    public Optional<OrderUserDetailReadModel> findUserOrderDetail(Long orderId, Long userId) {
        return repository.findOrderDetailByIdAndUserId(orderId, userId)
                .map(readModelMapper::toUserDetailed);
    }

    @Override
    public Optional<OrderAdminDetailReadModel> findAdminOrderDetail(Long orderId) {
        OrderDetailProjection orderProjection = repository.findOrderDetailById(orderId).orElseThrow();
        PaymentProjection paymentProjection = paymentJpaRepository.findAdminProjectionByOrderId(orderId);

        return Optional.of(readModelMapper.toAdminDetailed(orderProjection, paymentProjection));
    }

    @Override
    public boolean userHasActiveOrders(Long userId) {
        return repository.countActiveOrders(userId) > 0;
    }

    @Override
    public PageResult<AdminOrderListReadModel> findAdminOrders(PageQuery pageQuery, OrderQuery orderQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);
        Specification<OrderJpaEntity> specification = OrderSpecificationBuilder.from(orderQuery);

        Page<OrderListProjection> projections = repository.findBy(
                specification,
                q -> q.as(OrderListProjection.class).page(pageable)
        );
        return PageResultMapper.from(projections, readModelMapper::toAdminList);
    }

    @Override
    public PageResult<UserOrderListReadModel> findUserOrders(PageQuery pageQuery, Long userId) {
        Pageable pageable = PageableFactory.from(pageQuery);
        Page<OrderListProjection> projections = repository.findByUserId(pageable, userId);

        return PageResultMapper.from(projections, readModelMapper::toUserList);
    }

    public OrderAddressIdsProjection findOrderAddressIdByParcelId(Long parcelId) {
        return repository.findByParcelId(parcelId);
    }

    public boolean existsByIdAndUserId(Long orderId, Long userId) {
        return repository.existsByIdAndUserId(orderId, userId);
    }

    @Override
    public List<TaskItemCreationSnapshot> findOrdersByParcelIds(List<Long> parcelIds) {
        List<TaskItemCreationProjection> list = repository.findAllForTaskItemCreation(parcelIds);
        return list.stream().map(snapshotMapper::toSnapshot).toList();
    }
}
