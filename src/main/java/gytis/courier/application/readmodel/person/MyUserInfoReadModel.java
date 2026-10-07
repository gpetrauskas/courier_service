package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public record MyUserInfoReadModel(
        String name,
        String email,
        int orderCount,
        boolean subscribed,
        String defaultAddress,
        String phoneNumber,
        LocalDateTime createdDate
) implements MyInfoReadModel {
}
