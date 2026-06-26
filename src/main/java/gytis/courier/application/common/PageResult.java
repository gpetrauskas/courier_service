package gytis.courier.application.common;

import java.util.List;

public record PageResult<T>(
        List<T> data,
        int currentPage,
        long totalItems,
        int totalPages
) {
}
