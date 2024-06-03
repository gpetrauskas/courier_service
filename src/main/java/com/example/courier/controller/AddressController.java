package com.example.courier.controller;

import com.example.courier.domain.Address;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
      //  addressService.deleteAddress(id);
        return null;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myAddressList")
    public ResponseEntity<List<AddressDTO>> getMyAddressList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated.");
        }
        List<Address> myList = addressService.getAllMyAddresses(authentication.getName());
        List<AddressDTO> myAddressDTOs = myList.stream()
                .map(AddressMapper.INSTANCE::toAddressDTO)
                .toList();
        return ResponseEntity.ok(myAddressDTOs);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);

        return ResponseEntity.ok(updatedAddress);
    }
}
