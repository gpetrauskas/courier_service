package gytis.courier.domain.person;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String email) {
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        Objects.requireNonNull(email, "Email cannot be null");
        if (!PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

    @Override
    public String toString() {
        return email;
    }
}
