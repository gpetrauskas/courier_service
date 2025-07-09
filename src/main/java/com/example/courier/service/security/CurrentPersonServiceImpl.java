package com.example.courier.service.security;

import com.example.courier.domain.Person;
import com.example.courier.exception.UnauthorizedAccessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentPersonServiceImpl implements CurrentPersonService {
    private static final Logger logger = LoggerFactory.getLogger(CurrentPersonServiceImpl.class);
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private Person getAuthenticatedPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Person person)) {
            throw new UnauthorizedAccessException("Not logged in");
        }
        return person;
    }

    @Override
    public Person getCurrentPerson() {
        return getAuthenticatedPerson();
    }

    @Override
    public Long getCurrentPersonId() {
        return getAuthenticatedPerson().getId();
    }

    @Override
    public boolean isAdmin() {
        return getAuthenticatedPerson().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }
}
