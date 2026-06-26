package gytis.courier.adapter.in.rest.person.dto;

public record AdminPersonSearchRequest(
        int page,
        int size,
        String sortField,
        String direction,
        String role,
        String searchKey
) {
}
