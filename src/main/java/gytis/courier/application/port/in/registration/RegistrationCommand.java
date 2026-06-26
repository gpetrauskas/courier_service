package gytis.courier.application.port.in.registration;

import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.Password;

public record RegistrationCommand(
        String name,
        Email email,
        Password password
) {
}
