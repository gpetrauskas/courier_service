package gytis.courier.application.common;

public record PageQuery(
        int page,
        int size,
        String sortField,
        PageQueryDirection direction) {
}
