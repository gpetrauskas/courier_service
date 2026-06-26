package gytis.courier.adapter.in.pagination;

import gytis.courier.application.common.PageQueryDirection;

import java.util.Set;

public interface PagingPolicy {
    Set<String> allowedSortFields();
    String defaultSortField();
    PageQueryDirection defaultDirection();
}
