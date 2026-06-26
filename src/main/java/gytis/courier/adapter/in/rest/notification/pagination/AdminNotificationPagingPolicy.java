package gytis.courier.adapter.in.rest.notification.pagination;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum AdminNotificationPagingPolicy implements PagingPolicy {
    INSTANCE;

    @Override
    public Set<String> allowedSortFields() {
        return Set.of("createdAt", "id");
    }

    @Override
    public String defaultSortField() {
        return "createdAt";
    }

    @Override
    public PageQueryDirection defaultDirection() {
        return PageQueryDirection.ASC;
    }
}
