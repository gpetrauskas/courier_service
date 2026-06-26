package gytis.courier.adapter.in.rest.ticket;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.ticket.UserTicketQueryUseCase;
import gytis.courier.application.readmodel.ticket.TicketReadModel;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasRole('USER')")
public class UserTicketQueryController {
    private final UserTicketQueryUseCase queryUseCase;

    public UserTicketQueryController(UserTicketQueryUseCase queryUseCase) {
        this.queryUseCase = queryUseCase;
    }

    @GetMapping("/my")
    public PageResult<TicketReadModel> myTickets(Pageable pageable, @AuthenticationPrincipal AuthenticatedPerson person) {
        PageQuery pageQuery = PageQueryAssembler.fromPageable(pageable, TicketPagingPolicy.INSTANCE);
        return queryUseCase.myTickets(person.id(), pageQuery);
    }
}
