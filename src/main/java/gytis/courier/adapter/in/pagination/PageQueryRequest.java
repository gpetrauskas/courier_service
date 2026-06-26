package gytis.courier.adapter.in.pagination;

public record PageQueryRequest(
        int page,
        int size,
        String sortField,
        String direction
) {
}
