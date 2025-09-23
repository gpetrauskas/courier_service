package com.example.courier.repository;

import com.example.courier.domain.TaskItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long>, JpaSpecificationExecutor<TaskItem> {
    List<TaskItem> findByTaskId(Long taskId);

    @Query("SELECT DISTINCT i FROM TaskItem i JOIN FETCH i.task t JOIN FETCH t.items ti WHERE i.id = :itemId")
    Optional<TaskItem> findTaskItemWithTaskAndItemsById(@Param("itemId") Long itemId);

    @EntityGraph(attributePaths = {"task", "task.courier"})
    Optional<TaskItem> findTaskItemWithTaskAndCourierById(Long taskItemId);
}
