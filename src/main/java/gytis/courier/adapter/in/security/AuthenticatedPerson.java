package gytis.courier.adapter.in.security;

import java.security.Principal;

public record AuthenticatedPerson(Long id, String email, String role, String name) implements Principal {
    @Override
    public String getName() {
        return email;
    }
}