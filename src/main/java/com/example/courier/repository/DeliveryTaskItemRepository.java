package com.example.courier.repository;

import com.example.courier.domain.DeliveryTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DeliveryTaskItemRepository extends JpaRepository<DeliveryTaskItem, Long>, JpaSpecificationExecutor<DeliveryTaskItem> {
    List<DeliveryTaskItem> findByTaskId(Long taskId);

}
