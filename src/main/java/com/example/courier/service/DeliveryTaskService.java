package com.example.courier.service;

import com.example.courier.domain.DeliveryTask;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.repository.DeliveryTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class DeliveryTaskService {
    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private DeliveryTaskMapper deliveryTaskMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryTaskDTO> getAllDeliveryLists() {
        List<DeliveryTask> task = deliveryTaskRepository.findAll();
        return task.stream()
                .map(deliveryTaskMapper::toDeliveryTaskDTO)
                .toList();
    }
}
