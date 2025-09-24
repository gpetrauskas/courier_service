package com.example.courier.validation.task;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.CreateTaskDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskValidator {
    public void validateCreation(CreateTaskDTO dto, Courier courier, List<Parcel> parcels) {
        if (courier.hasActiveTask()) {
            throw new IllegalArgumentException("Courier already has active task.");
        }

        if (parcels.size() != dto.parcelsIds().size()) {
            throw new IllegalArgumentException("Some parcels not found.");
        }

        parcels.forEach(p -> {
            if (p.isAssigned()) {
                throw new IllegalArgumentException("Parcel ID " + p.getId() + " unavailable");
            }
        });
    }



}
