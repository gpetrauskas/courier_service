package gytis.courier.adapter.in.rest.task.policy;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum AdminTaskPagingPolicy implements PagingPolicy {
    INSTANCE;

    @Override
    public Set<String> allowedSortFields() {
        return Set.of("id", "createdAt", "completedAt");
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
