package com.example.courier.controller;

import com.example.courier.dto.AddressDTO;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.service.address.AddressService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private Logger log = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

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
        try {
            addressService.deleteAddressById(id);

            return ResponseEntity.ok().body("Address was successfully deleted.");
        } catch (EntityNotFoundException | AccessDeniedException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
}
