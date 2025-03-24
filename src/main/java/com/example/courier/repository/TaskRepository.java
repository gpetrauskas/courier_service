package com.example.courier.repository;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT t from Task t JOIN FETCH t.courier c JOIN FETCH t.items i JOIN FETCH i.parcel p")
    List<Task> findAllWithDetails();

    @EntityGraph(attributePaths = {"items", "items.senderAddress", "items.recipientAddress", "courier"})
    List<Task> findByCourierIdAndDeliveryStatus(Long courierId, DeliveryStatus deliveryStatus);

    List<Task> findByCourierIdAndDeliveryStatusIn(Long courierId, Set<DeliveryStatus> statuses);

    Page<Task> findByCourierIdAndDeliveryStatusIn(Long courierId, Set<DeliveryStatus> statuses, Pageable pageable);



}
