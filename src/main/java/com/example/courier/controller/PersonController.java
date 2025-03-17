package com.example.courier.controller;

import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.PersonResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.service.person.PersonService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PaginatedResponseDTO<PersonResponseDTO> fetchAllPersons(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String searchKeyword) {
        return personService.findAllPaginated(page, size, role, searchKeyword);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findPersonById/{id}")
    public ResponseEntity<PersonResponseDTO> findPersonById(@PathVariable Long id) {
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
        this.personService.delete(id);
        return ResponseEntity.ok("Person was successfully deleted.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/banUnban/{id}")
    public ResponseEntity<String> banUnban(@PathVariable Long id) {
        String action = this.personService.banUnban(id);
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

}
