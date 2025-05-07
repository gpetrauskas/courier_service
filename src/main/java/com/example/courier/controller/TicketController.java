package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.ticket.TicketCommentRequestDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.request.ticket.TicketUpdateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;
import com.example.courier.dto.response.ticket.TicketCommentResponseDTO;
import com.example.courier.service.ticket.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticketManagement")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO> create(@RequestBody @Valid TicketCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(ticketService.create(requestDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TicketBase> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PaginatedResponseDTO<? extends TicketBase>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long personid
    ) {
        return ResponseEntity.ok(ticketService.getAll(pageable, status, personid));
    }

    @PostMapping("/addComment")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TicketCommentResponseDTO> addComment(@RequestBody @Valid TicketCommentRequestDTO commentRequestDTO) {
        return ResponseEntity.ok(ticketService.addComment(commentRequestDTO));
    }

    @GetMapping("/{ticketId}/getComments")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PaginatedResponseDTO<TicketCommentResponseDTO>> getComments(
            @PathVariable Long ticketId,
            @RequestParam int currentPage,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(ticketService.getComments(ticketId, currentPage, pageSize));
    }

    @PutMapping("/{ticketId}/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO>close(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.close(ticketId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> update(@RequestBody TicketUpdateRequestDTO requestDTO) {
        ticketService.updateTicket(requestDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "ok"));
    }
}
