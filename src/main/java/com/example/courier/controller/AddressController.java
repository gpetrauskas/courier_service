package com.example.courier.controller;

import com.example.courier.dto.AddressDTO;
import com.example.courier.service.address.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myAddressList")
    public ResponseEntity<List<AddressDTO>> getMyAddressList() {
        return ResponseEntity.ok(addressService.getAllMyAddresses());
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.updateAddress(id, addressDTO));
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> removeAddress(@PathVariable Long id) {
        addressService.deleteAddressById(id);
        return ResponseEntity.ok("Address was successfully deleted.");
    }
}
