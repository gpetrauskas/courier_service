package gytis.courier.adapter.in.rest.common;

import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PaginatedResponseMapper {
    private PaginatedResponseMapper() {}

    public static <T,R> PaginatedResponse<R> wrap(Page<T> page, Function<T,R> mapperFn) {
        var responses = page
                .map(mapperFn)
                .toList();

        return new PaginatedResponse<>(
                responses,
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
