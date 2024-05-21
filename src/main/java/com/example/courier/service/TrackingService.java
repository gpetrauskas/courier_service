package com.example.courier.service;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Package;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @Autowired
    private OrderRepository orderRepository;

    public PackageStatus getPackageStatus(String trackingNumber) {
        try {
            Order trackedOrder = orderRepository.findByPackageDetails_TrackingNumber(trackingNumber);
            if (trackedOrder != null) {
                return trackedOrder.getPackageDetails().getStatus();
            } else {
                throw new OrderNotFoundException("Order with tracking number: " + trackingNumber + " not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching package status", e);
        }
    }

    public void updatePackageStatus(String trackingNumber, String newStatus) {

    }
}
