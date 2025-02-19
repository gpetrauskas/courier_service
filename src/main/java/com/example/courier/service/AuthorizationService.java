package com.example.courier.service;

import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.util.AuthUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void validateCourierTaskAssignment(DeliveryTaskItem taskItem) throws UnauthorizedAccessException {
        Long authenticatedCourierId = AuthUtils.getAuthenticatedPersonId();

        if (!taskItem.getTask().getCourier().getId().equals(authenticatedCourierId)) {
            throw new UnauthorizedAccessException("Not authorized to update this item");
        }
    }
}
