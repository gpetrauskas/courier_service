package com.example.courier.service.permission;

import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;

public interface PermissionService {
    boolean hasTicketAccess(Person person, Ticket ticket);
}
