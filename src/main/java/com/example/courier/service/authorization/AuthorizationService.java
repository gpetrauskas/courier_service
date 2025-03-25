package com.example.courier.service.authorization;

import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    public void validateCourierTaskAssignmentByTaskItem(TaskItem taskItem) throws UnauthorizedAccessException {
        Long authenticatedCourierId = getAuthenticatedPersonId();

        if (!taskItem.getTask().getCourier().getId().equals(authenticatedCourierId)) {
            throw new UnauthorizedAccessException("Not authorized to update this item");
        }
    }

    public void validateCourierTaskAssignment(Task task) throws UnauthorizedAccessException {
        Long authenticatedCourierId = getAuthenticatedPersonId();

        log.info("check if ids match");
        if (!task.getCourier().getId().equals(authenticatedCourierId)) {
            throw new UnauthorizedAccessException("Not authorized to update this task");
        }
    }

    private Long getAuthenticatedPersonId() {
        return AuthUtils.getAuthenticatedPersonId();

    }
}
