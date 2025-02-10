package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.repository.DeliveryTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private DeliveryTaskMapper deliveryTaskMapper;

    public List<DeliveryTaskDTO> getCurrentTaskList(Long courierId) {
        List<DeliveryTask> list = deliveryTaskRepository.findByCourierIdAndDeliveryStatus(courierId, DeliveryStatus.IN_PROGRESS);

        return list.stream()
                .map(deliveryTaskMapper::toDeliveryTaskDTO)
                .toList();
    }

}
