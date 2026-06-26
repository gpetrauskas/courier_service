package gytis.courier.application.readmodel.person;

public record MyAdminInfoReadModel(
        String name,
        String email,
        int createdTasks
) implements MyInfoReadModel {
}
