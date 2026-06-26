package gytis.courier.adapter.in.rest.personnotification;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum PersonNotificationPagingPolicy implements PagingPolicy {
    INSTANCE;


    @Override
    public Set<String> allowedSortFields() {
        return Set.of("receivedAt", "isRead");
    }

    @Override
    public String defaultSortField() {
        return "receivedAt";
    }

    @Override
    public PageQueryDirection defaultDirection() {
        return PageQueryDirection.ASC;
    }
}
