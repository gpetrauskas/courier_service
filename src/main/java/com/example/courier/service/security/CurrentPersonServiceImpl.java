package com.example.courier.service.security;

import com.example.courier.domain.Person;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.service.person.PersonService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentPersonServiceImpl implements CurrentPersonService {
    private static final Logger logger = LoggerFactory.getLogger(CurrentPersonServiceImpl.class);
    private final PersonService personService;

    public CurrentPersonServiceImpl(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public Person getCurrentPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Person)) {
            throw new UnauthorizedAccessException("Not logged in");
        }
            Long id = ((Person) auth.getPrincipal()).getId();
            return personService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public Long getCurrentPersonId() {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return person.getId();
    }

    @Override
    public boolean isAdmin() {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return person.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
