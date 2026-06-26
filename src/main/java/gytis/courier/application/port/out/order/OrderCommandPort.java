package gytis.courier.application.port.out.order;

import gytis.courier.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderCommandPort {
    Order insert(Order order);
    void save(Order order);

    Optional<Order> getBasicById(Long id);
    Optional<Order> getForUser(Long orderId, Long userId);
    Optional<Order> getWithParcel(Long id);
    Optional<Order> getWithParcelAndAddresses(Long id);
    List<Order> findAllByParcelIds(List<Long> ids);
    // Optional<Order> getWithDetails(Long id);
    // OrderAddress getOrderAddressById(Long id);
}
