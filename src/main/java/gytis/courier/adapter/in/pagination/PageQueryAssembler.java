package gytis.courier.adapter.in.pagination;

import gytis.courier.application.common.PageQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageQueryAssembler {
    private PageQueryAssembler() {}

    public static PageQuery from(
            int page,
            int size,
            String sortField,
            String direction,
            PagingPolicy policy
    ) {
        return PageQueryFactory.from(
                page,
                size,
                sortField,
                direction,
                policy.allowedSortFields(),
                policy.defaultSortField(),
                policy.defaultDirection()
        );
    }

    public static PageQuery fromPageable(Pageable pageable, PagingPolicy policy) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        String sortField = null;
        String direction = null;

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortField = order.getProperty();
            direction = order.isDescending() ? "DESC" : "ASC";
        }

        return from(page, size, sortField, direction, policy);
    }
}
