package gytis.courier.application.readmodel.person;

public record MyUserInfoReadModel(
        String name,
        String email,
        int orderCount,
        boolean subscribed,
        String defaultAddress,
        String phoneNumber
) implements MyInfoReadModel {
}
