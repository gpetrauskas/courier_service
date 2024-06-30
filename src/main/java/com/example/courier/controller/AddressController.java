package com.example.courier.controller;

import com.example.courier.domain.Address;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.repository.AddressRepository;
import com.example.courier.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private Logger log = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    private AddressService addressService;
    @Autowired
    private AddressRepository addressRepository;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myAddressList")
    public ResponseEntity<List<AddressDTO>> getMyAddressList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Address> myList = addressService.getAllMyAddresses(authentication.getName());
        List<AddressDTO> myAddressDTOs = myList.stream()
                .map(AddressMapper.INSTANCE::toAddressDTO)
                .toList();
        return ResponseEntity.ok(myAddressDTOs);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO, Principal principal) {
        try {
            Address address = addressRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Address not found"));

            if (!principal.getName().equals(address.getUser().getEmail())) {
                throw new AccessDeniedException("Invalid request");
            }
            if (!id.equals(addressDTO.id())) {
                throw new IllegalArgumentException("Cannot update address");
            }

            AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);

            return ResponseEntity.ok(updatedAddress);
        } catch (EntityNotFoundException | AccessDeniedException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred.");
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeAddress(@PathVariable Long id, Principal principal) {
        try {
            Address address = addressRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Address not found"));

            if (!principal.getName().equals(address.getUser().getEmail())) {
                throw new AccessDeniedException("Invalid request");
            }

            if (!id.equals(address.getId())) {
                throw new IllegalArgumentException("Cannot delete this.");
            }

            addressRepository.deleteById(id);

            return ResponseEntity.ok().body("Address was successfully deleted.");
        } catch (EntityNotFoundException | AccessDeniedException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
}
