package com.example.courier.dto;

import com.example.courier.domain.User;

public record UserResponseDTO(Long id, String name, String email, boolean isBlocked) {
    public static UserResponseDTO fromUser(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.isBlocked());
    }
}
