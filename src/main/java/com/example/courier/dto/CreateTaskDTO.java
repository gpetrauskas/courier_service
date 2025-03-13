package com.example.courier.dto;

import com.example.courier.validation.shared.NotEmptyField;

import java.util.List;

public record CreateTaskDTO(
        Long courierId,
        Long adminId,
        @NotEmptyField List<Long> parcelsIds,
        @NotEmptyField String taskType) {
}
