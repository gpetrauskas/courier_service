package com.example.courier.repository;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @EntityGraph(attributePaths = {"items", "items.parcel", "courier"})
    List<Task> findAllWithDetailsByIdIn(List<Long> taskIds);

    @EntityGraph(attributePaths = {"items", "items.parcel", "courier"})
    Optional<Task> findWithDetailsById(Long taskId);

    @EntityGraph(attributePaths = {"items", "items.senderAddress", "items.recipientAddress", "courier"})
    List<Task> findByCourierIdAndDeliveryStatus(Long courierId, DeliveryStatus deliveryStatus);

    @EntityGraph(attributePaths = {"items", "items.senderAddress", "items.recipientAddress"})
    List<Task> findByCourierIdAndDeliveryStatusIn(Long courierId, Set<DeliveryStatus> statuses);

    @EntityGraph(attributePaths = {"items", "items.parcel", "courier"})
    Page<Task> findByCourierIdAndDeliveryStatusIn(Long courierId, Set<DeliveryStatus> statuses, Pageable pageable);

    @EntityGraph(attributePaths = {"courier", "items"})
    Optional<Task> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {"items", "courier"})
    @Query("SELECT t FROM Task t JOIN t.items i WHERE i.id = :itemId")
    Optional<Task> findTaskByItemIdWithItems(@Param("itemId") Long itemId);

    @Query("SELECT DISTINCT t from Task t JOIN FETCH t.courier c JOIN FETCH t.items i JOIN FETCH i.parcel p WHERE t.id = :id")
    Optional<Task> findWithCourierItemsAndParcelsById(@Param("id") Long id);

    @Query("SELECT DISTINCT t " +
            "from Task t " +
            "JOIN FETCH t.items i " +
            "WHERE t.courier.id = :courierId " +
                "AND t.deliveryStatus IN :statuses " +
                "AND i.status NOT IN :hiddenStatuses")
    List<Task> findByCourierIdAndDeliveryStatusIn(
            @Param("courierId") Long courierId,
            @Param("statuses") Set<DeliveryStatus> statuses,
            @Param("hiddenStatuses") List<ParcelStatus> hiddenStatuses
            );


}
