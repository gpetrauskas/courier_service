package gytis.courier.adapter.in.rest.common;

import gytis.courier.application.common.PageResult;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        int currentPage,
        long totalItems,
        int totalPages
) {
    public static <T> PaginatedResponse<T> from(PageResult<T> pageResult) {
        return new PaginatedResponse<>(
                pageResult.data(),
                pageResult.currentPage(),
                pageResult.totalItems(),
                pageResult.totalPages()
        );
    }
}
