package com.example.courier.service.permission;

import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;
import org.springframework.stereotype.Service;

@Service
public class DefaultPermissionService implements PermissionService {

    @Override
    public boolean hasTicketAccess(Person person, Ticket ticket) {
        return ticket.getCreatedBy().getId().equals(person.getId())
                || person.getRole().equals("ADMIN");
    }
}
