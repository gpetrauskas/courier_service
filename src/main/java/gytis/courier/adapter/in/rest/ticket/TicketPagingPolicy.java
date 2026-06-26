package gytis.courier.adapter.in.rest.ticket;

import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public enum TicketPagingPolicy implements PagingPolicy {
    INSTANCE;


    @Override
    public Set<String> allowedSortFields() {
        return Set.of("updatedAt", "createdAt");
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
