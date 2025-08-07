package com.example.courier.dto;

import com.example.courier.domain.User;

public record UserWithOrdersCountByStatus(User user, int confirmedOrdersCount) {
}
