package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.service.person.PersonService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService personService;
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PaginatedResponseDTO<AdminPersonResponseDTO> fetchAllPersons(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        return personService.findAllPaginated(page, size, role, searchKeyword, sortBy, direction);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findPersonById/{id}")
    public ResponseEntity<AdminPersonResponseDTO> findPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public ResponseEntity<String> updatePerson(@PathVariable Long id,
                                               @RequestBody PersonDetailsUpdateRequest updateRequest) {
        personService.updateDetails(id, updateRequest);
        return ResponseEntity.ok("Person was successfully updated.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.ok("Person was successfully deleted.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/banUnban/{id}")
    public ResponseEntity<String> banUnban(@PathVariable Long id,
                                           @RequestBody(required = false)  BanActionRequestDTO requestDTO) {
        String action = personService.banUnban(id, requestDTO);
        return ResponseEntity.ok(action);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/availableCouriers")
    public ResponseEntity<List<CourierDTO>> getAvailableCouriers() {
        List<CourierDTO> list = personService.getAvailableCouriers();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/availableCouriersCount")
    public ResponseEntity<Long> availableCouriersCount() {
        Long count = personService.availableCouriersCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{personId}/banHistory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BanHistoryDTO>> banHistory(@PathVariable Long personId) {
        return ResponseEntity.ok(personService.getBanHistory(personId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PersonResponseDTO> myInfo() {
        return ResponseEntity.ok(personService.myInfo());
    }

    @PutMapping("/editMyInfo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO> editMyInfo(@RequestBody @Valid UserEditDTO userEditDTO) {
        logger.info("Endpoint invoked");
        return ResponseEntity.ok(personService.updateMyInfo(userEditDTO));
    }

    @PutMapping("password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'COURIER')")
    public ResponseEntity<ApiResponseDTO> changePassword(@RequestBody @Valid PasswordChangeDTO dto) {
        return ResponseEntity.ok(personService.changePassword(dto));
    }
}
