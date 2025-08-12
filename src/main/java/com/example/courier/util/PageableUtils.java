package com.example.courier.util;

import com.example.courier.dto.PaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;


public final class PageableUtils {
    private PageableUtils() {}

    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        page = Math.max(page, 0);
        size = size <= 0 ? 10 : Math.min(size, 40);

        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = Sort.by(dir, sortBy);

        return PageRequest.of(page, size, sort);
    }

    public static <T, C> PaginatedResponseDTO<T> toPaginatedResponse(
            List<T> responseList, Page<C> pageList) {
        return new PaginatedResponseDTO<T>(
                responseList,
                pageList.getNumber(),
                pageList.getTotalElements(),
                pageList.getTotalPages()
        );
    }

    public static <T> PaginatedResponseDTO<T> empty() {
        return new PaginatedResponseDTO<>(Collections.emptyList(), 0, 0, 0);
    }
}
