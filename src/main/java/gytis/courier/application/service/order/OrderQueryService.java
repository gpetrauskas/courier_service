package gytis.courier.application.service.order;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.order.OrderQueryUseCase;
import gytis.courier.application.port.out.order.OrderQueryPort;
import gytis.courier.application.query.filter.OrderQuery;
import gytis.courier.application.readmodel.order.AdminOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderUserDetailReadModel;
import gytis.courier.application.readmodel.order.UserOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderAdminDetailReadModel;
import gytis.courier.application.readmodel.order.OrderForTaskReadModel;
import gytis.courier.domain.address.AddressType;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskType;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class OrderQueryService implements OrderQueryUseCase {
    private final OrderQueryPort queryPort;

    public OrderQueryService(OrderQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    // user
    @Override
    public PageResult<UserOrderListReadModel> getUserOrderList(PageQuery pageQuery, Long userId) {
        return queryPort.findUserOrders(pageQuery, userId);
    }

    @Override
    public OrderUserDetailReadModel getUserOrderDetail(Long orderId, Long userId) {
        return queryPort.findUserOrderDetail(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order was not found"));
    }


    // admin
    @Override
    public OrderAdminDetailReadModel getOrderDetail(Long orderId) {
        return queryPort.findAdminOrderDetail(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public PageResult<OrderForTaskReadModel> getAllByTaskType(PageQuery pageQuery, TaskType type) {
        AddressType addressType;
        Set<ParcelStatus> statuses = new HashSet<>();
        switch (type) {
            case TaskType.PICKUP -> {
               statuses.add(ParcelStatus.PICKING_UP);
               statuses.add(ParcelStatus.FAILED_PICKUP);
                addressType = AddressType.senderAddress;
            }
            case TaskType.DELIVERY -> {
                statuses.add(ParcelStatus.PICKED_UP);
                statuses.add(ParcelStatus.FAILED_DELIVERY);
                addressType = AddressType.recipientAddress;
            }
            default -> throw new ResourceNotFoundException("Wrong task type");
        };

        return queryPort.findAllForTask(pageQuery, statuses, addressType);
    }

    @Override
    public PageResult<AdminOrderListReadModel> getAdminOrderList(PageQuery pageQuery, OrderQuery orderQuery) {
        System.out.println(pageQuery.direction() + " " + pageQuery.sortField());
        System.out.println(orderQuery.orderStatus() + " " + orderQuery.id());
        return queryPort.findAdminOrders(pageQuery, orderQuery);
    }
}
