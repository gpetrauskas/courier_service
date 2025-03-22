package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery-options")
public class DeliveryMethodController {

    @Autowired
    private DeliveryMethodService deliveryMethodService;

    @GetMapping
    public ResponseEntity<Map<String, List<DeliveryMethodDTO>>> getAllDeliveryOptions() {
        Map<String, List<DeliveryMethodDTO>> deliveryOptions = deliveryMethodService.getAllDeliveryOptions();
        return ResponseEntity.ok(deliveryOptions);
    }

    @GetMapping("/notCategorized")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryMethodAdminResponseDTO>> getAllDeliveryOptionsNotCategorized() {
        List<DeliveryMethodAdminResponseDTO> list = deliveryMethodService.getDeliveryOptionsNotCategorized();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> updateDeliveryOption(@PathVariable Long id, @Valid @RequestBody UpdateDeliveryMethodDTO deliveryOptionDTO) {
        deliveryMethodService.updateDeliveryOption(id, deliveryOptionDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery option (id:" + id + ") successfully updated."));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> add(@Valid @RequestBody CreateDeliveryMethodDTO createDeliveryMethodDTO) {
        deliveryMethodService.addNewDeliveryOption(createDeliveryMethodDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "New Delivery Option was added successfully"));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        deliveryMethodService.deleteDeliveryOption(id);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery Option with id " + id + " was deleted successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryMethodDTO> getById(@PathVariable Long id) {
        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.getById(id);

        return ResponseEntity.ok(deliveryMethodDTO);
    }

}
