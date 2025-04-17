package com.example.courier.repository;

import com.example.courier.domain.PersonNotification;
import com.example.courier.domain.keys.PersonNotificationId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PersonNotificationRepository extends JpaRepository<PersonNotification, PersonNotificationId>, JpaSpecificationExecutor<PersonNotification> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE PersonNotification pn 
        SET pn.isRead = true, 
            pn.readAt = CURRENT_TIMESTAMP 
        WHERE pn.id.personId = :personId 
          AND pn.id.notificationId = :notificationId
    """)
    void markAsRead(@Param("personId") Long personId,
                    @Param("notificationId") Long notificationId);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
        INSERT INTO person_notifications 
            (person_id, notification_id, is_read, received_at)
        SELECT p.id, :notificationId, FALSE, CURRENT_TIMESTAMP
        FROM persons p
        WHERE p.id IN (:recipientIds)
    """, nativeQuery = true)
    void bulkInsert(@Param("notificationId") Long notificationId,
                    @Param("recipientIds") List<Long> recipientIds);

    @Modifying
    @Query("UPDATE PersonNotification pn SET pn.isRead = true, pn.readAt = :now " +
            "WHERE pn.id.personId = :personId AND pn.id.notificationId IN :ids AND pn.isRead = false")
    int markMultipleAsRead(@Param("personId") Long personId,
                           @Param("ids") List<Long> notificationsIds,
                           @Param("now")LocalDateTime now);

    @Query("SELECT pn FROM PersonNotification pn WHERE pn.id.notificationId = :notificationId " +
            "AND pn.id.personId = :personId")
    Optional<PersonNotification> findByIdAndPersonId(@Param("notificationId") Long notificationId, @Param("personId") Long personId);

    @Modifying
    @Query("DELETE FROM PersonNotification pn WHERE pn.notification.id = :notificationId AND pn.person.id = :personId")
    int deleteByNotificationIdAndPersonId(@Param("notificationId") Long notificationId,@Param("personId") Long personId);
    int deleteAllByPersonId(Long personId);

    @Modifying
    @Query("DELETE FROM PersonNotification pn WHERE pn.notification.id IN :ids AND pn.person.id = :personId")
    int deleteMultipleByIdAndPersonId(@Param("ids") List<Long> ids,@Param("personId") Long personId);
}

