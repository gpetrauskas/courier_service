package com.example.courier.controller;

import com.example.courier.domain.DeliveryOption;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.deliveryoption.CreateDeliveryOptionDTO;
import com.example.courier.dto.request.deliveryoption.UpdateDeliveryOptionDTO;
import com.example.courier.dto.response.deliveryoption.DeliveryOptionDTO;
import com.example.courier.service.deliveryoption.DeliveryOptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery-options")
public class DeliveryOptionController {

    @Autowired
    private DeliveryOptionService deliveryOptionService;

    @GetMapping
    public ResponseEntity<Map<String, List<DeliveryOptionDTO>>> getAllDeliveryOptions() {
        Map<String, List<DeliveryOptionDTO>> deliveryOptions = deliveryOptionService.getAllDeliveryOptions();
        return ResponseEntity.ok(deliveryOptions);
    }

    @GetMapping("/notCategorized")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryOption>> getAllDeliveryOptionsNotCategorized() {
        List<DeliveryOption> list = deliveryOptionService.getDeliveryOptionsNotCategorized();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> updateDeliveryOption(@PathVariable Long id, @Valid @RequestBody UpdateDeliveryOptionDTO deliveryOptionDTO) {
        deliveryOptionService.updateDeliveryOption(id, deliveryOptionDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery option (id:" + id + ") successfully updated."));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> add(@Valid @RequestBody CreateDeliveryOptionDTO createDeliveryOptionDTO) {
        deliveryOptionService.addNewDeliveryOption(createDeliveryOptionDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "New Delivery Option was added successfully"));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        deliveryOptionService.deleteDeliveryOption(id);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery Option with id " + id + " was deleted successfully"));
    }

}
