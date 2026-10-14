package gytis.courier.adapter.in.rest.activitylog;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.pagination.PagingPolicy;
import gytis.courier.adapter.in.rest.activitylog.policy.ActivityLogPolicy;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageQueryDirection;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.activityLog.ActivityLogQueryUseCase;
import gytis.courier.application.query.filter.ActivityLogQuery;
import gytis.courier.application.readmodel.activitylog.ActivityLogReadModel;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {
    private final ActivityLogQueryUseCase useCase;

    public ActivityLogController(ActivityLogQueryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public PageResult<ActivityLogReadModel> findAllLogs(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute ActivityLogSearchRequest request
    ) {
        PageQuery pageQuery = PageQueryAssembler.fromPageable(pageable, ActivityLogPolicy.INSTANCE);
        ActivityLogQuery filterQuery = new ActivityLogQuery(request.role(), request.keyword());

        System.out.println(request.role());
        System.out.println(request);

        return useCase.getAll(pageQuery, filterQuery);
    }

    @GetMapping("/task")
    public PageResult<ActivityLogReadModel> findLogsForTask(
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        PageQuery query = PageQueryAssembler.from(page, size, "createdAt", "desc", ActivityLogPolicy.INSTANCE);
        return useCase.getForTasks(query);
    }
}
