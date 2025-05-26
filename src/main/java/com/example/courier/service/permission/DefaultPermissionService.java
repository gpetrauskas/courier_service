package com.example.courier.service.permission;

import com.example.courier.domain.Address;
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

    @Override
    public boolean hasAddressAccess(Person person, Address address) {
        return address.getUser().getId().equals(person.getId())
                || person.getRole().equals("ADMIN");
    }
}
