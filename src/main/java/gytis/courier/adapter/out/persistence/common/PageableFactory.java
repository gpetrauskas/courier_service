package gytis.courier.adapter.out.persistence.common;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageQueryDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableFactory {
    private PageableFactory() {}

    public static Pageable from(PageQuery query) {
        Sort sort = toSort(query);

        return PageRequest.of(query.page(), query.size(), sort);
    }

    private static Sort toSort(PageQuery query) {
        if (query.sortField() == null || query.sortField().isBlank()) {
            return Sort.unsorted();
        }

        return Sort.by(
                (query.direction() == PageQueryDirection.ASC)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC,
                query.sortField()
        );
    }
}
