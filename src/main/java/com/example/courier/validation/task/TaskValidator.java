package com.example.courier.validation.task;

import com.example.courier.common.DeliveryStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskValidator {

    public void validateCheckInStatus(DeliveryStatus status) {
        if (DeliveryStatus.isCheckInAllowed(status)) {
            throw new IllegalArgumentException(String.format("Status %s invalid for check-in", status));
        }
    }

    public void validateAdminUpdatable() {

    }

}
