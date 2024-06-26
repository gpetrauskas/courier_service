package com.example.courier.dto;

import com.example.courier.domain.User;

public record UserResponseDTO(String name, String email) {
    public static UserResponseDTO fromUser(User user) {
        return new UserResponseDTO(user.getName(), user.getEmail());
    }
}
