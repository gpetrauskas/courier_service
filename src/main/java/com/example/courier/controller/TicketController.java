package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.ticket.TicketCommentRequestDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;
import com.example.courier.dto.response.ticket.TicketCommentResponseDTO;
import com.example.courier.service.ticket.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<TicketBase>> getAll() {
        return ResponseEntity.ok(ticketService.getAll());
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
}
