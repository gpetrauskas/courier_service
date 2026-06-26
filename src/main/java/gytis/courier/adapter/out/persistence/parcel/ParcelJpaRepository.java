package gytis.courier.adapter.out.persistence.parcel;

import gytis.courier.domain.order.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParcelJpaRepository extends JpaRepository<ParcelJpaEntity, Long> {
    Optional<ParcelStatus> findByTrackingNumber(String trackingNumber);
    Long countByStatusAndAssignedFalse(ParcelStatus status);

    Long countByAssignedFalseAndStatusIn(List<ParcelStatus> statuses);

    @Modifying
    @Query("UPDATE ParcelJpaEntity p SET p.status = :status WHERE p.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") ParcelStatus status);

    @Modifying
    @Query("UPDATE ParcelJpaEntity p SET p.assigned = true WHERE p.id IN :parcelIds")
    int markAssigned(@Param("parcelIds") List<Long> parcelIds);

    @Modifying
    @Query("UPDATE ParcelJpaEntity p SET p.assigned = false WHERE p.id IN :parcelIds")
    int markUnassigned(@Param("parcelIds") List<Long> parcelIds);

    @Modifying
    @Query("UPDATE ParcelJpaEntity p SET p.status = :status WHERE p.id IN :ids")
    void updateStatusByIds(@Param("status") ParcelStatus status, @Param("ids") List<Long> ids);
}
