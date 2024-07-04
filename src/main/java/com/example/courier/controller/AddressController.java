package com.example.courier.controller;

import com.example.courier.dto.AddressDTO;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.service.AddressService;
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
    public ResponseEntity<List<AddressDTO>> getMyAddressList(Principal principal) {
        List<AddressDTO> myList = addressService.getAllMyAddresses(principal.getName());

        return ResponseEntity.ok(myList);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO, Principal principal) {
        try {
            AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO, principal);

            return ResponseEntity.ok(updatedAddress);
        } catch (AddressNotFoundException e) {
            log.error("Address with id {} not found", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UserAddressMismatchException e) {
            log.error("Address with id {} does not belong to user with email {}", id, principal.getName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred.");
        } catch (Exception e) {
            log.error("Error updating address with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeAddress(@PathVariable Long id, Principal principal) {
        try {
            addressService.deleteAddressById(id, principal);

            return ResponseEntity.ok().body("Address was successfully deleted.");
        } catch (EntityNotFoundException | AccessDeniedException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
}
