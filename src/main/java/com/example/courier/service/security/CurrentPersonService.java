package com.example.courier.service.security;

import com.example.courier.domain.Person;

public interface CurrentPersonService {

    Person getCurrentPerson();
    Long getCurrentPersonId();
    <T extends Person> T getCurrentPersonAs(Class<T> tClass);
    boolean isAdmin();
}
