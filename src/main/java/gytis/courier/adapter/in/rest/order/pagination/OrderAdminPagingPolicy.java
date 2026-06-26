package gytis.courier.adapter.in.rest.order.pagination;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum OrderAdminPagingPolicy implements PagingPolicy {
    INSTANCE;

    @Override
    public Set<String> allowedSortFields() {
        return Set.of("id", "status", "createDate");
    }

    @Override
    public String defaultSortField() {
        return "id";
    }

    @Override
    public PageQueryDirection defaultDirection() {
        return PageQueryDirection.ASC;
    }
}
