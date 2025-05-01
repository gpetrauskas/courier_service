package com.example.courier.service.permission;

import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;

public interface PermissionService {
    boolean canAddTicketComment(Person person, Ticket ticket);
    boolean canReadTicketComments(Person person, Ticket ticket);
}
