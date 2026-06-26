package gytis.courier.domain.person;

import java.util.Objects;

public record Password(String password) {
    public Password {
        Objects.requireNonNull(password, "Password cannot be null");
        if (password.length() < 8 || password.length() > 12) throw new IllegalArgumentException("Password must be 6-12 char length");
        if (password.chars().noneMatch(Character::isUpperCase)) throw new IllegalArgumentException("Password must have minimum 1 upper letter");
        if (password.chars().noneMatch(Character::isLowerCase)) throw new IllegalArgumentException("Password must have at least 1 lower case");
        if (password.chars().noneMatch(Character::isDigit)) throw new IllegalArgumentException("Password must have at least 1 digid");
    }
}
