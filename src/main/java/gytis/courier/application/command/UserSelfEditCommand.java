package gytis.courier.application.command;

import gytis.courier.domain.person.PhoneNumber;

public record UserSelfEditCommand(
        Long userId,
        PhoneNumber phoneNumber,
        Long defaultAddressId,
        Boolean subscribed
) {
}
