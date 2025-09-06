package com.example.courier.util;

import com.example.courier.dto.PaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


public final class PageableUtils {
    private PageableUtils() {}

    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        page = Math.max(page, 0);
        size = size <= 0 ? 10 : Math.min(size, 40);

        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = Sort.by(dir, sortBy);

        return PageRequest.of(page, size, sort);
    }

    public static Pageable pageForItemIndex(int index, int size, Sort sort) {
        int pageNumber = Math.max(index / size, 0);
        size = size <= 0 ? 10 : Math.min(size, 40);
        return PageRequest.of(pageNumber, size, sort);
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

    public static <E, D> PaginatedResponseDTO<D> mapPage(Page<E> page, Function<E, D> mapper) {
        return new PaginatedResponseDTO<D>(page, mapper);
    }

    public static <E, D> PaginatedResponseDTO<D> pageContainingItem(
            Long itemId,
            int pageSize,
            Sort sort,
            Function<Long, Optional<Integer>> positionFinder,
            Function<Pageable, Page<E>> pageFetcher,
            Function<E, D> mapper,
            Supplier<? extends RuntimeException> notFoundExceptionSupplier
    ) {
        int itemPosition = positionFinder.apply(itemId).orElseThrow(notFoundExceptionSupplier);
        Pageable pageable = pageForItemIndex(itemPosition, pageSize, sort);

        Page<E> page = pageFetcher.apply(pageable);
        List<D> dtoList = page.map(mapper).getContent();

        return new PaginatedResponseDTO<>(dtoList, page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    public static <T> PaginatedResponseDTO<T> empty() {
        return new PaginatedResponseDTO<>(Collections.emptyList(), 0, 0, 0);
    }
}
