package gytis.courier.adapter.out.persistence.common;

import gytis.courier.application.common.PageResult;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageResultMapper {
    private PageResultMapper() {}

    public static <T, R> PageResult<R> from(Page<T> page, Function<T, R> mapper) {
        return new PageResult<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
