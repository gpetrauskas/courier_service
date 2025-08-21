package com.example.courier.service.permission;

import com.example.courier.domain.Address;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;
import com.example.courier.service.security.CurrentPersonService;
import org.springframework.stereotype.Service;

@Service
public class DefaultPermissionService implements PermissionService {

    private final CurrentPersonService currentPersonService;

    public DefaultPermissionService(CurrentPersonService currentPersonService) {
        this.currentPersonService = currentPersonService;
    }

    @Override
    public boolean hasTicketAccess(Person person, Ticket ticket) {
        return ticket.getCreatedBy().getId().equals(person.getId())
                || person.getRole().equals("ADMIN");
    }

    @Override
    public boolean hasAddressAccess(Person person, Address address) {
        return address.getUser().getId().equals(person.getId())
                || person.getRole().equals("ADMIN");
    }

    @Override
    public boolean hasPaymentMethodAccess(PaymentMethod paymentMethod) {
        return paymentMethod.getUser().getId().equals(currentUserId());
    }

    private Long currentUserId() {
        return currentPersonService.getCurrentPersonId();
    }
}
