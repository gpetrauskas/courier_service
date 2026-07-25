package gytis.courier.application.port.in.order;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.OrderQuery;
import gytis.courier.application.readmodel.order.AdminOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderUserDetailReadModel;
import gytis.courier.application.readmodel.order.UserOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderAdminDetailReadModel;
import gytis.courier.application.readmodel.order.OrderForTaskReadModel;
import gytis.courier.domain.order.OrderStatus;
import gytis.courier.domain.payment.PaymentStatus;
import gytis.courier.domain.task.TaskType;

import java.util.List;

public interface OrderQueryUseCase {
    List<Long> findOrderIdsByStatusAndPaymentStatus(OrderStatus orderStatus, PaymentStatus paymentStatus);

    //admin
    PageResult<AdminOrderListReadModel> getAdminOrderList(PageQuery pageQuery, OrderQuery orderQuery);
    OrderAdminDetailReadModel getOrderDetail(Long id);
    PageResult<OrderForTaskReadModel> getAllByTaskType(PageQuery pageQuery, TaskType type);

    //user
    PageResult<UserOrderListReadModel> getUserOrderList(PageQuery pageQuery, Long userId);
    OrderUserDetailReadModel getUserOrderDetail(Long id, Long userId);
}
