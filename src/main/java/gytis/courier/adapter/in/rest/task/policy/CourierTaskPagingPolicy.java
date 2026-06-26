package gytis.courier.adapter.in.rest.task.policy;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum CourierTaskPagingPolicy implements PagingPolicy {
    INSTANCE;

    @Override
    public Set<String> allowedSortFields() {
        return Set.of("createdAt", "completedAt", "taskType");
    }

    @Override
    public String defaultSortField() {
        return "completedAt";
    }

    @Override
    public PageQueryDirection defaultDirection() {
        return PageQueryDirection.ASC;
    }
}
