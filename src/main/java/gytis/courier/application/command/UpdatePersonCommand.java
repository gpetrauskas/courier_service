package gytis.courier.application.command;

import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.PhoneNumber;

public record UpdatePersonCommand(
        String name,
        Email email,
        PhoneNumber phoneNumber
) {
}
