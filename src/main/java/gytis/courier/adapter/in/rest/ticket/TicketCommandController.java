package gytis.courier.adapter.in.rest.ticket;

import gytis.courier.adapter.in.rest.ticket.dto.AddCommentRequest;
import gytis.courier.adapter.in.rest.ticket.dto.TicketCreateRequest;
import gytis.courier.adapter.in.rest.ticket.dto.TicketRequestMapper;
import gytis.courier.adapter.in.rest.ticket.dto.TicketUpdateRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.ticket.TicketCommandUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("isAuthenticated()")
public class TicketCommandController {
    private final TicketRequestMapper requestMapper;
    private final TicketCommandUseCase useCase;

    public TicketCommandController(TicketRequestMapper requestMapper, TicketCommandUseCase useCase) {
        this.requestMapper = requestMapper;
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid TicketCreateRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        useCase.create(requestMapper.toCommandCreate(request, person.id()));
        return ResponseEntity.ok(new ApiResponse("success", "Ticket was successfully created."));
    }

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<ApiResponse> addComment(@PathVariable Long ticketId, @RequestBody AddCommentRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        useCase.addComment(requestMapper.toCommentAddCommand(ticketId, person.id(), person.role(), request));
        return ResponseEntity.ok(new ApiResponse("success", "Comment was added successfully"));
    }

    @PatchMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> update(@PathVariable Long ticketId, @RequestBody @Valid TicketUpdateRequest request) {
        System.out.println(request.status() + " " + request.priority());
        useCase.update(ticketId, requestMapper.toUpdateCommand(request));
        return ResponseEntity.ok(new ApiResponse("success", "Ticket was updated"));
    }

    @PatchMapping("/{ticketId}/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> close(@PathVariable Long ticketId, @AuthenticationPrincipal AuthenticatedPerson person) {
        useCase.close(ticketId, person.id());
        return ResponseEntity.ok(new ApiResponse("success", "Ticket was closed successfully"));
    }
}
