package com.example.courier.service.permission;

import com.example.courier.domain.Address;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;
import com.example.courier.repository.projection.TicketAccessIdsProjection;

public interface PermissionService {
    boolean hasTicketAccess(Person person, Ticket ticket);
    boolean hasTicketAccess(TicketAccessIdsProjection accessIdsProjection);
    boolean hasAddressAccess(Person person, Address address);
    boolean hasPaymentMethodAccess(PaymentMethod paymentMethod);
}
