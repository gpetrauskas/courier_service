package gytis.courier.domain.person;

public record PhoneNumber(String number) {
    public PhoneNumber {
        if (number != null && !number.isBlank()) {
            if (number.length() != 8) throw new IllegalArgumentException("Phone number must contain 8 digits");
            if (!number.chars().allMatch(Character::isDigit)) {
                throw new IllegalArgumentException("Phone number must be only digits");
            }
        }
    }
}
