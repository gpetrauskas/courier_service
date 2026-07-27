package gytis.courier.adapter.in.rest.activitylog.policy;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;


public enum ActivityLogPolicy implements PagingPolicy {
    INSTANCE;

    @Override
    public Set<String> allowedSortFields() {
        return Set.of("createdAt", "role", "action");
    }

    @Override
    public String defaultSortField() {
        return "createdAt";
    }

    @Override
    public PageQueryDirection defaultDirection() {
        return PageQueryDirection.DESC;
    }
}
