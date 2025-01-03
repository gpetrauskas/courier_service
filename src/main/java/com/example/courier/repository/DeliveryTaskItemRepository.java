package com.example.courier.repository;

import com.example.courier.domain.DeliveryTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryTaskItemRepository extends JpaRepository<DeliveryTaskItem, Long> {

}
