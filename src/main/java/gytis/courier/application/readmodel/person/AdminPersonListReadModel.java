package gytis.courier.application.readmodel.person;

public record AdminPersonListReadModel(
        Long id,
        String name,
        String email,
        boolean banned,
        boolean deleted,
        String role
) {
}
