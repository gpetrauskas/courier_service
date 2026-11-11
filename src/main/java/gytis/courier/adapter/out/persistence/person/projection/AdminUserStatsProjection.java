package gytis.courier.adapter.out.persistence.person.projection;

public interface AdminUserStatsProjection {
    long getNewUsers();
    long getActiveUsers();
    long getBlockedUsers();
}
