package gytis.courier;

import gytis.courier.domain.person.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = { "badEmail@", "" })
     void throwsExceptionOnWrongEmail(String wrongEmail) {
        assertThrows(IllegalArgumentException.class, () -> {
            Email email = new Email(wrongEmail);
        });
    }

    @Test
    void throwsExceptionOnNullEmail() {
        assertThrows(NullPointerException.class, () -> {
            Email email = new Email(null);
        });
    }

    @Test
    void successfullyCreatedEmail() {
        assertDoesNotThrow(() -> {
            Email email = new Email("goodEmail@example.com");
        });
    }
}
