package gytis.courier.adapter.in.rest.ticket;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.ticket.AdminTicketQueryUseCase;
import gytis.courier.application.readmodel.ticket.AdminTicketReadModel;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tickets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketQueryController {
    private final AdminTicketQueryUseCase queryUseCase;

    public AdminTicketQueryController(AdminTicketQueryUseCase queryUseCase) {
        this.queryUseCase = queryUseCase;
    }

    @GetMapping
    public PageResult<AdminTicketReadModel> all(Pageable pageable) {
        PageQuery pageQuery = PageQueryAssembler.fromPageable(pageable, TicketPagingPolicy.INSTANCE);
        return queryUseCase.allTickets(pageQuery);
    }
}
