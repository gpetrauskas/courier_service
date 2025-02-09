package com.example.courier.service;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Package;
import com.example.courier.dto.PackageDTO;
//import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    @Autowired
    private PackageRepository packageRepository;
   // @Autowired
  //  private CourierRepository courierRepository;


    public List<PackageDTO> getAllPackagesToPickUp() {

            List<Package> packageList = packageRepository.findByStatus(PackageStatus.PICKING_UP);
            List<PackageDTO> packageDTOList = packageList.stream()
                    .map(PackageDTO::packageToDTO).toList();

        return List.of();
    }

}
