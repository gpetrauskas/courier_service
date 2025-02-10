package com.example.courier.service;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Order;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @Autowired
    private OrderRepository orderRepository;

    public ParcelStatus getParcelStatus(String trackingNumber) {
        try {
            Order trackedOrder = orderRepository.findByParcelDetails_TrackingNumber(trackingNumber);
            if (trackedOrder != null) {
                return trackedOrder.getParcelDetails().getStatus();
            } else {
                throw new ResourceNotFoundException("Order with tracking number: " + trackingNumber + " not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching parcel status", e);
        }
    }

    public void updateParcelStatus(String trackingNumber, String newStatus) {

    }
}
