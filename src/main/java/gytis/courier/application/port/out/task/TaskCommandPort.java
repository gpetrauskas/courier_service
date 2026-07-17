package gytis.courier.application.port.out.task;

import gytis.courier.domain.task.Task;

import java.util.Optional;

public interface TaskCommandPort {
    Optional<Task> getById(Long id);
    Optional<Task> getWithItemsById(Long id);
    void update(Task task);
    void updateWithItems(Task task);
    Task create(Task task);
}
