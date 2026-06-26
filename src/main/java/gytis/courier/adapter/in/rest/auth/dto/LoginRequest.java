package gytis.courier.adapter.in.rest.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
