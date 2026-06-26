package gytis.courier.domain.person;

public interface DeletionPolicy {
    boolean canDeleteUser(Long userId);
}
