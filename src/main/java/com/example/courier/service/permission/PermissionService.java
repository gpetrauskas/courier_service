package com.example.courier.service.permission;

import com.example.courier.domain.Address;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;

public interface PermissionService {
    boolean hasTicketAccess(Person person, Ticket ticket);
    boolean hasAddressAccess(Person person, Address address);
    boolean hasPaymentMethodAccess(PaymentMethod paymentMethod);
}
