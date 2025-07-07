package com.example.courier.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {
    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = Sort.by(dir, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
