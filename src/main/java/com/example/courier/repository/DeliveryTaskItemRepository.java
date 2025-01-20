package com.example.courier.repository;

import com.example.courier.domain.DeliveryTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryTaskItemRepository extends JpaRepository<DeliveryTaskItem, Long> {
    List<DeliveryTaskItem> findByTaskId(Long taskId);
}
