package gytis.courier.adapter.out.persistence.task;

import gytis.courier.application.port.out.task.TaskCommandPort;
import gytis.courier.domain.task.Task;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class TaskCommandAdapter implements TaskCommandPort {
    private final TaskEntityMapper taskMapper;
    private final TaskItemEntityMapper itemMapper;
    private final TaskJpaRepository taskRepository;

    public TaskCommandAdapter(TaskEntityMapper taskMapper, TaskItemEntityMapper itemMapper, TaskJpaRepository taskRepository) {
        this.taskMapper = taskMapper;
        this.itemMapper = itemMapper;
        this.taskRepository = taskRepository;
    }

    @Override
    public Optional<Task> getById(Long taskId) {
        return taskRepository.findById(taskId).map(taskMapper::toDomain);
    }

    @Override
    public Optional<Task> getWithItemsById(Long taskId) {
        return taskRepository.findWithItems(taskId).map(taskMapper::toDomainWithItems);
    }

    @Override
    public void create(Task task) {
        TaskJpaEntity taskEntity = taskMapper.toEntity(task);
        taskRepository.save(taskEntity);
    }

    @Transactional
    @Override
    public void update(Task task) {
        TaskJpaEntity managed = taskRepository.findById(task.getId()).orElseThrow();
        taskMapper.update(task, managed);
    }

    @Transactional
    @Override
    public void updateWithItems(Task task) {
        TaskJpaEntity managed = taskRepository.findWithItems(task.getId()).orElseThrow();
        taskMapper.updateWithItems(task, managed);

        task.getTaskItems().forEach(domainItem ->
                managed.getItems().stream()
                        .filter(e -> e.getId().equals(domainItem.getId()))
                        .findFirst()
                        .ifPresent(e -> itemMapper.update(domainItem, e)));
    }
}
