package com.example.courier.dto;

import com.example.courier.validation.shared.NotNullOrEmpty;

import java.util.List;

public record CreateTaskDTO(
        Long courierId,
        Long adminId,
        @NotNullOrEmpty List<Long> parcelsIds,
        @NotNullOrEmpty String taskType) {
}
