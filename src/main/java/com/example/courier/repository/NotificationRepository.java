package com.example.courier.repository;

import com.example.courier.domain.Notification;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

 @Query("""
         SELECT
          n.id as id,
          n.title as title,
          n.message as message,
          n.createdAt as createdAt,
          pn.readAt as readAt,
          pn.isRead as isRead
         FROM PersonNotification pn
         JOIN pn.notification n
         WHERE pn.person.id = :recipientId
         ORDER BY n.createdAt DESC
         """)
 Page<NotificationWithReadStatus> findAllByRecipientIdPageable(
         @Param("recipientId") Long recipientId, Pageable pageable);
}
