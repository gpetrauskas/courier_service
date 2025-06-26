package com.example.courier.service.authorization;

import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final CurrentPersonService currentPersonService;

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    AuthorizationService(CurrentPersonService currentPersonService) {
        this.currentPersonService = currentPersonService;
    }

    public void validateCourierTaskAssignmentByTaskItem(TaskItem taskItem) throws UnauthorizedAccessException {
        if (!taskItem.getTask().getCourier().getId().equals(currentPersonService.getCurrentPersonId())) {
            throw new UnauthorizedAccessException("Not authorized to update this item");
        }
    }

    public void validateCourierTaskAssignment(Task task) throws UnauthorizedAccessException {
        log.info("check if ids match");
        if (!task.getCourier().getId().equals(currentPersonService.getCurrentPersonId())) {
            throw new UnauthorizedAccessException("Not authorized to update this task");
        }
    }
}
