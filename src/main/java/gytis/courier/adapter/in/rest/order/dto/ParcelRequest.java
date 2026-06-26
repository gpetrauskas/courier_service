package gytis.courier.adapter.in.rest.order.dto;

public record ParcelRequest(
        Long weightId,
        Long dimensionsId,
        String contents
) {
}
