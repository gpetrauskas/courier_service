package gytis.courier.adapter.in.rest.task;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.rest.task.policy.CourierTaskPagingPolicy;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.task.CourierTaskQueryUseCase;
import gytis.courier.application.readmodel.task.CTaskListReadModel;
import gytis.courier.application.readmodel.task.CTaskHistoryReadModel;
import gytis.courier.application.readmodel.task.CTaskReadModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courier/tasks")
@PreAuthorize("hasRole('COURIER')")
public class CourierTaskQueryController {
    private final CourierTaskQueryUseCase useCase;

    public CourierTaskQueryController(CourierTaskQueryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/history")
    public PageResult<CTaskListReadModel> getHistory(@PageableDefault Pageable pageable, @AuthenticationPrincipal AuthenticatedPerson person) {
        PageQuery query = PageQueryAssembler.fromPageable(pageable, CourierTaskPagingPolicy.INSTANCE);
        return useCase.getMyTaskHistory(query, person.id());
    }

    @GetMapping("/assigned")
    public PageResult<CTaskListReadModel> getAssigned(@AuthenticationPrincipal AuthenticatedPerson person) {
        return useCase.getAllAssigned(person.id());
    }

    @GetMapping("/{id}/history")
    public CTaskHistoryReadModel getDetailedHistory(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        return useCase.getDetailedHistoryTask(id, person.id());
    }

    @GetMapping("/{id}/current")
    public CTaskReadModel currentTask(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        return useCase.getCurrentTask(id, person.id());
    }
}
