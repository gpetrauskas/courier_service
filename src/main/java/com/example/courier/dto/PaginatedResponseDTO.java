package com.example.courier.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PaginatedResponseDTO<T>(
        List<T> data,
        int currentPage,
        long totalItems,
        int totalPages
) {
    public <E> PaginatedResponseDTO(Page<E> page, Function<E, T> mapper) {
        this(page.getContent().stream().map(mapper).toList(),
                page.getNumber(), page.getTotalElements(), page.getTotalPages()
        );
    }
}
