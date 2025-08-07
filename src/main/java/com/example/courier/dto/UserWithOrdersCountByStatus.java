package com.example.courier.dto;

import com.example.courier.domain.User;

public record UserWithConfirmedOrdersDTO(User user, long confirmedOrdersCount) {
}
