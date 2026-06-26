package gytis.courier.adapter.in.pagination;

import gytis.courier.application.common.PageQueryDirection;
import gytis.courier.application.common.PageQuery;

import java.util.Set;

public final class PageQueryFactory {

    private PageQueryFactory() {}

    public static PageQuery from(
            int page,
            int size,
            String sortField,
            String dir,
            Set<String> allowedFields,
            String defaultSortField,
            PageQueryDirection defaultDirection
    ) {
        String resolvedSortField = resolveSortField(sortField, allowedFields, defaultSortField);
        PageQueryDirection resolvedSortDirection = resolveDirection(dir, defaultDirection);

        return new PageQuery(
                page,
                size,
                resolvedSortField,
                resolvedSortDirection
        );
    }

    private static PageQueryDirection resolveDirection(String dir, PageQueryDirection defaultDirection) {
        return (dir == null || dir.isBlank())
                ? defaultDirection
                : PageQueryDirection.parseDirection(dir);
    }

    private static String resolveSortField(String sortField, Set<String> allowedList, String defaultSortField) {
        String requested = (sortField == null || sortField.isBlank())
                ? defaultSortField
                : sortField.trim();

        return allowedList.stream()
                .filter(f -> f.equalsIgnoreCase(requested))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Invalid sort field " + requested + ". Allowed: " + allowedList)
                );
    }
}
