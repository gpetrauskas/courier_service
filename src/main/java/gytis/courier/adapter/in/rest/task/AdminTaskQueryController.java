package gytis.courier.adapter.in.rest.task;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.rest.task.dto.AdminTaskFilterRequest;
import gytis.courier.adapter.in.rest.task.dto.TaskQueryRequestMapper;
import gytis.courier.adapter.in.rest.task.policy.AdminTaskPagingPolicy;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.task.AdminTaskQueryUseCase;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskQueryController {
    private final AdminTaskQueryUseCase useCase;
    private final TaskQueryRequestMapper requestMapper;

    public AdminTaskQueryController(AdminTaskQueryUseCase useCase, TaskQueryRequestMapper requestMapper) {
        this.useCase = useCase;
        this.requestMapper = requestMapper;
    }

    @GetMapping
    public PageResult<TaskListReadModel> all(@ModelAttribute AdminTaskFilterRequest request) {
        AdminTaskQueryFilter queryFilter = requestMapper.toQueryFilter(request);
        PageQuery pageQuery = PageQueryAssembler.from(request.page(), request.size(), request.sortBy(), request.direction(), AdminTaskPagingPolicy.INSTANCE);

        return useCase.getAll(queryFilter, pageQuery);
    }

    @GetMapping("/{id}")
    public AdminTaskReadModel getDetailed(@PathVariable Long id) {
        return useCase.getDetailedTask(id);
    }
}
