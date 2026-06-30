package gytis.courier;

import gytis.courier.domain.person.Password;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = { "toShort", "wayyyyyyyytooLonngPASSWORD123", "onlyLetters", "12345678"})
    void throwExceptionOnInvalidPassword(String badPass) {
        assertThrows(IllegalArgumentException.class, () -> {
            Password password = new Password(badPass);
        });
    }

    @Test
    void throwExceptionOnNull() {
        assertThrows(NullPointerException.class, () -> {
            Password password = new Password(null);
        });
    }

    @Test
    void successfullyCreatedPassword() {
        assertDoesNotThrow(() -> {
            Password password = new Password("goodPass123");
        });
    }
}
