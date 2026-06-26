package gytis.courier.adapter.in.rest.order.pagination;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum OrderUserPagingPolicy implements PagingPolicy {
    INSTANCE;

    @Override
    public Set<String> allowedSortFields() {
        return Set.of("createDate", "status");
    }

    @Override
    public String defaultSortField() {
        return "createDate";
    }

    @Override
    public PageQueryDirection defaultDirection() {
        return PageQueryDirection.DESC;
    }
}
