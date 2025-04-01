package com.example.courier.dto.response;

import java.time.LocalDateTime;

public record BanHistoryDTO(
        Long id,
        boolean banned,
        String actionBy,
        String reason,
        LocalDateTime actionTime
) {

}
