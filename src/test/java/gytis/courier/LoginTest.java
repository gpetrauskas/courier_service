package gytis.courier;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.auth.LoginCommand;
import gytis.courier.application.port.in.auth.AuthTokens;
import gytis.courier.application.port.out.auth.PasswordHashingPort;
import gytis.courier.application.port.out.auth.TokenGeneratorPort;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.application.service.auth.AuthTokenIssuer;
import gytis.courier.application.service.auth.LoginService;
import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginTest {
    @Mock
    PasswordHashingPort passwordHashingPort;
    @Mock
    PersonCommandPort personCommandPort;
    @Mock
    ActivityLogUseCase logUseCase;
    @Mock
    AuthTokenIssuer authTokenIssuer;
    @InjectMocks
    LoginService loginService;

    private final LoginCommand command = new LoginCommand("me@example.com", "goodPass123");

    private final User user = new User(1L, "me me", new Email("me@example.com"), "encodedPass");

    @Test
    void throwOnUserNotFound() {
        when(personCommandPort.findByEmail(command.email()))
                .thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> loginService.login(command));
    }

    @Test
    void throwOnPasswordDoNotMatch() {
        when(personCommandPort.findByEmail(command.email()))
                .thenReturn(Optional.of(user));
        when(passwordHashingPort.matches(command.password(), user.getPassword()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> loginService.login(command));
    }

    @Test
    void throwOnPersonIsBlocked() {
        User blocked = new User(1L, "blocked", new Email("blocked@example.com"), "encodedPass");
        when(personCommandPort.findByEmail(command.email()))
                .thenReturn(Optional.of(blocked));
        when(passwordHashingPort.matches(command.password(), blocked.getPassword()))
                .thenReturn(true);

        blocked.banUnban();

        assertThrows(IllegalArgumentException.class, () -> loginService.login(command));
    }

    @Test
    void successfullyLogIn() {
        when(personCommandPort.findByEmail(command.email()))
                .thenReturn(Optional.of(user));
        when(passwordHashingPort.matches(command.password(), user.getPassword()))
                .thenReturn(true);
        when(authTokenIssuer.issue(any(), any(), any(), any())).thenReturn(new AuthTokens("xx", "xxx"));

        AuthTokens result = loginService.login(command);

        verify(authTokenIssuer).issue(any(), any(), any(), any());

        assertNotNull(result);
    }
}
