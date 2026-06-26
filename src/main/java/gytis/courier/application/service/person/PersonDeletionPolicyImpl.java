package gytis.courier.application.service.person;

import gytis.courier.application.port.out.order.OrderQueryPort;
import gytis.courier.domain.person.DeletionPolicy;
import org.springframework.stereotype.Service;

@Service
public class PersonDeletionPolicyImpl implements DeletionPolicy {
    private final OrderQueryPort orderPort;

    public PersonDeletionPolicyImpl(OrderQueryPort orderPort) {
        this.orderPort = orderPort;
    }

    @Override
    public boolean canDeleteUser(Long userId) {
        return !orderPort.userHasActiveOrders(userId);
    }
}
