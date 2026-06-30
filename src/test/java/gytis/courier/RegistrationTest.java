package gytis.courier;

import gytis.courier.application.port.in.registration.RegistrationCommand;
import gytis.courier.application.port.out.auth.PasswordHashingPort;
import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.application.service.regstration.RegistrationService;
import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.Password;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationTest {

    private final RegistrationCommand command = new RegistrationCommand(
            "me example",
            new Email("me@example.com"),
            new Password("goodPass123")
    );
    @Mock private PersonQueryPort personQueryPort;
    @Mock private UserCommandPort userCommandPort;
    @Mock private PasswordHashingPort passwordHashingPort;
    @InjectMocks private RegistrationService registrationService;

    @Test
    void throwsExceptionOnAlreadyExistingEmail() {
        when(personQueryPort.existsByEmail("me@example.com"))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> registrationService.register(command));
    }

    @Test
    void successfullyRegister() {
        when(personQueryPort.existsByEmail("me@example.com"))
                .thenReturn(false);

        assertDoesNotThrow(() -> registrationService.register(command));

        verify(userCommandPort).create(any());
        verify(passwordHashingPort).encode(command.password().password());
    }
}
