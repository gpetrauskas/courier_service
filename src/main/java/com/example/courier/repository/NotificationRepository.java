package com.example.courier.repository;

import com.example.courier.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
 /*   @Query("SELECT n FROM Notification n JOIN n.recipients r WHERE r.id = :personId ORDER BY n.createdAt DESC")
    List<Notification> findAllByRecipientId(@Param("personId") Long personId);

    @Query("SELECT n FROM Notification n JOIN n.recipients r WHERE r.id = :recipientId ORDER BY n.createdAt DESC")
    Page<Notification> findAllByRecipientIdPageable(@Param("recipientId") Long recipientId, Pageable pageable);
*/
 @Query("SELECT pn.notification FROM PersonNotification pn WHERE pn.person.id = :recipientId ORDER BY pn.notification.createdAt DESC")
 Page<Notification> findAllByRecipientIdPageable(@Param("recipientId") Long recipientId, Pageable pageable);
}
