package com.example.courier.service.security;

import com.example.courier.domain.Person;

public interface CurrentPersonService {

    Person getCurrentPerson();
    Long getCurrentPersonId();
    boolean isAdmin();
}
