package com.example.courier.repository;

import com.example.courier.domain.PersonNotification;
import com.example.courier.domain.keys.PersonNotificationId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}

