package gytis.courier.adapter.in.security;

public record AuthenticatedPerson(Long id, String email, String role, String name) {
}