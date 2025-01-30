package com.example.courier.dto;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> data,
        int currentPage,
        long totalItems,
        int totalPages
) {
}
