package com.example.courier.dto.jwt;

public record JwtClaims(
        String subject,
        String role,
        String name,
        String authToken
) {
}
