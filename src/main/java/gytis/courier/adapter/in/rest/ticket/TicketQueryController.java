package gytis.courier.adapter.in.rest.ticket;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.ticket.TicketCommentQueryUseCase;
import gytis.courier.application.readmodel.ticket.TicketCommentReadModel;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class TicketQueryController {
    private final TicketCommentQueryUseCase commentUseCase;

    public TicketQueryController(TicketCommentQueryUseCase commentUseCase) {
        this.commentUseCase = commentUseCase;
    }

    @GetMapping("/{ticketId}/comments")
    public PageResult<TicketCommentReadModel> getComments(@PathVariable Long ticketId, Pageable pageable, @AuthenticationPrincipal AuthenticatedPerson person) {
        PageQuery query = PageQueryAssembler.fromPageable(pageable, TicketCommentPagingPolicy.INSTANCE);
        return commentUseCase.getComments(ticketId, person.id(), person.role(), query);
    }
}
