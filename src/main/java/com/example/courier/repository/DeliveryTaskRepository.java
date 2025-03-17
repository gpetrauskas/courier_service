package com.example.courier.repository;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.DeliveryTask;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long>, JpaSpecificationExecutor<DeliveryTask> {

    @Query("SELECT t from DeliveryTask t JOIN FETCH t.courier c JOIN FETCH t.items i JOIN FETCH i.parcel p")
    List<DeliveryTask> findAllWithDetails();

    @EntityGraph(attributePaths = {"items", "items.senderAddress", "items.recipientAddress", "courier"})
    List<DeliveryTask> findByCourierIdAndDeliveryStatus(Long courierId, DeliveryStatus deliveryStatus);

    @EntityGraph(attributePaths = {"items", "items.senderAddress", "items.recipientAddress", "courier"})
    List<DeliveryTask> findByCourierIdAndDeliveryStatusIn(Long courierId, Set<DeliveryStatus> statuses);


}
