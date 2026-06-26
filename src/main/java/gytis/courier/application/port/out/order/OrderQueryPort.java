package gytis.courier.application.port.out.order;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.OrderQuery;
import gytis.courier.application.readmodel.order.AdminOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderUserDetailReadModel;
import gytis.courier.application.readmodel.order.UserOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderAdminDetailReadModel;
import gytis.courier.application.readmodel.order.OrderForTaskReadModel;
import gytis.courier.domain.address.AddressType;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskItemCreationSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderQueryPort {
    //OrderAddressIdsProjection findOrderAddressIdByParcelId(Long parcelId);
    //boolean existsByIdAndUserId(Long orderId, Long userId);

    //command
    List<TaskItemCreationSnapshot> findOrdersByParcelIds(List<Long> parcelIds);
    boolean userHasActiveOrders(Long userId);

    // admin query
    PageResult<AdminOrderListReadModel> findAdminOrders(PageQuery pageQuery, OrderQuery orderQuery);
    Optional<OrderAdminDetailReadModel> findAdminOrderDetail(Long orderId);
    PageResult<OrderForTaskReadModel> findAllForTask(PageQuery pageQuery, Set<ParcelStatus> statuses, AddressType type);

    // user query
    PageResult<UserOrderListReadModel> findUserOrders(PageQuery pageQuery, Long userId);
    Optional<OrderUserDetailReadModel> findUserOrderDetail(Long orderId, Long userId);

}
