package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public record UserReadModel(
        Long id,
        String name,
        String email,
        String phoneNumber,
        boolean blocked,
        boolean deleted,
        LocalDateTime deletedDate
) {
}
