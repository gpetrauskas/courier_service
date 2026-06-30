package gytis.courier;

import gytis.courier.domain.person.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = { "12345", "123456789", "letters", " " })
    void throwExceptionOnBadPhoneNumber(String wrongPhoneNumber) {
        assertThrows(IllegalArgumentException.class, () -> {
            PhoneNumber phoneNumber = new PhoneNumber(wrongPhoneNumber);
        });
    }

    @Test
    void successfullyCreatedPhoneNumberOrNull() {
        assertDoesNotThrow(() -> {
            PhoneNumber phoneNumber = new PhoneNumber("12345678");
        });
    }
}
