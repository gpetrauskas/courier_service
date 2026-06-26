package gytis.courier.adapter.out.persistence.notification.personnotification;

import gytis.courier.adapter.out.persistence.notification.projection.PersonNotificationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PersonNotificationJpaRepository extends JpaRepository<PersonNotificationJpaEntity, PersonNotificationJpaId> {
    List<PersonNotificationJpaEntity> findByIdPersonId(Long id);
    PersonNotificationJpaEntity findByIdPersonIdAndIdNotificationId(Long personId, Long notificationId);

    void deleteByIdPersonIdAndIdNotificationId(Long personId, Long notificationId);
    void deleteAllByIdPersonId(Long personId);

    @Query("SELECT COUNT(*) FROM PersonNotificationJpaEntity pn WHERE pn.isRead = false AND pn.id.personId = :personId")
    long findUnreadCount(@Param("personId") Long personId);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO person_notifications (person_id, notification_id, is_read, received_at)
            SELECT p.id, :nId, FALSE, CURRENT_TIMESTAMP FROM persons p WHERE p.id IN (:personIds)
            """, nativeQuery = true)
    void bulkDeliverToRecipients(@Param("nId") Long notificationId, @Param("personIds") List<Long> personIds);

    @Modifying
    @Query("UPDATE PersonNotificationJpaEntity pn SET pn.isRead = true, pn.readAt = CURRENT_TIMESTAMP WHERE pn.isRead = false AND pn.id.personId = :personId")
    void markAllAsRead(@Param("personId") Long personId);

    @Modifying
    @Query("UPDATE PersonNotificationJpaEntity pn SET pn.isRead = true, pn.readAt = CURRENT_TIMESTAMP WHERE pn.isRead = false AND pn.id.notificationId = :id AND pn.id.personId = :personId")
    void markAsRead(@Param("id") Long id, @Param("personId") Long personId);

    @Query("SELECT COUNT(pn) FROM PersonNotificationJpaEntity pn WHERE pn.id.personId = :personId AND pn.receivedAt > " +
            "(SELECT pn2.receivedAt FROM PersonNotificationJpaEntity pn2 " +
            "WHERE pn2.id.notificationId = :notificationId AND pn2.id.personId = :personId)")
    Optional<Integer> findNotificationPosition(@Param("personId") Long personId, @Param("notificationId") Long notificationId);


    @Query("""
    SELECT
        pn.id.notificationId AS notificationId,
        n.title AS title,
        n.message AS message,
        pn.isRead AS read,
        pn.receivedAt AS receivedAt
    FROM PersonNotificationJpaEntity pn
    JOIN NotificationJpaEntity n
        ON n.id = pn.id.notificationId
    WHERE pn.id.personId = :personId
    ORDER BY pn.receivedAt desc
    """)
    Page<PersonNotificationProjection> findAllByPersonId(@Param("personId") Long personId, Pageable pageable);

    /**
     * used only by admins
     */
    int deleteByIdNotificationId(Long notificationId);

}
