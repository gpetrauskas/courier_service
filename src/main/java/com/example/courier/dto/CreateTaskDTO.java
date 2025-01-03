package com.example.courier.dto;

import java.util.List;

public record CreateTaskDTO(Long courierId, Long adminId, List<Long> parcelsIds, String taskType) {
}
