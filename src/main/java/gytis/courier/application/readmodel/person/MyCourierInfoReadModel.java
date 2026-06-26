package gytis.courier.application.readmodel.person;

public record MyCourierInfoReadModel(
        String name,
        String email,
        boolean activeTask
) implements MyInfoReadModel {
}
