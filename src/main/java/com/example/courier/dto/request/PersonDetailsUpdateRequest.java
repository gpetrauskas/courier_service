package com.example.courier.dto.request;

public record PersonDetailsUpdateRequest(
        Long id,
        String name,
        String email,
        String role,
        String phoneNumber,
        boolean isBlocked
) {
}
