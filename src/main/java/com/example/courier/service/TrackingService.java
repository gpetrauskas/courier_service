package com.example.courier.service;

import com.example.courier.domain.Order;
import com.example.courier.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @Autowired
    private OrderRepository orderRepository;

    public String getPackageStatus(String trackingNumber) {
        try {
            Order trackedOrder = orderRepository.findByTrackingNumber(trackingNumber);
            if (trackedOrder != null) {
                return trackedOrder.getPackageDetails().getStatus();
            }
        } catch (Exception e) {
        }
    }

    public void updatePackageStatus(String trackingNumber, String newStatus) {

    }
}
