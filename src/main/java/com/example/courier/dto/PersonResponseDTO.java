package com.example.courier.dto;

import com.example.courier.domain.Person;

public record PersonResponseDTO(Long id, String name, String email, boolean isBlocked) {
    public static PersonResponseDTO fromPerson(Person user) {
        return new PersonResponseDTO(user.getId(), user.getName(), user.getEmail(), user.isBlocked());
    }
}
