package gytis.courier.application.readmodel.person;

public record AdminUserStatsReadModel(
        long newUsersCount,
        long activeUsersCount,
        long blockedUsersCount
) {
}
