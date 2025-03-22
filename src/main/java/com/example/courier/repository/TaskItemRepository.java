package com.example.courier.repository;

import com.example.courier.domain.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long>, JpaSpecificationExecutor<TaskItem> {
    List<TaskItem> findByTaskId(Long taskId);

}
