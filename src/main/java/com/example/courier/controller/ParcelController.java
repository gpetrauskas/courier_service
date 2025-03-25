package com.example.courier.controller;

import com.example.courier.service.parcel.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/parcel")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getAvailableParcelsCount() {
        return ResponseEntity.ok(parcelService.getAvailableParcelsCount());
    }

}
