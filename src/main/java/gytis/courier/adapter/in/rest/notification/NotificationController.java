package gytis.courier.adapter.in.rest.notification;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.rest.notification.dto.NotificationCreateRequest;
import gytis.courier.adapter.in.rest.notification.dto.AdminNotificationQueryRequest;
import gytis.courier.adapter.in.rest.notification.pagination.AdminNotificationPagingPolicy;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.application.port.in.notification.NotificationQueryUseCase;
import gytis.courier.application.query.filter.AdminNotificationQuery;
import gytis.courier.application.readmodel.notification.NotificationReadModel;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.adapter.in.rest.common.ApiResponseType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {
    private final NotificationCommandUseCase commandUseCase;
    private final NotificationQueryUseCase queryUseCase;
    private final NotificationRequestMapper mapper;

    public NotificationController(NotificationCommandUseCase commandUseCase, NotificationQueryUseCase queryUseCase, NotificationRequestMapper mapper) {
        this.commandUseCase = commandUseCase;
        this.queryUseCase = queryUseCase;
        this.mapper = mapper;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody NotificationCreateRequest request) {
        System.out.println(request.message() + " " + request.title() + " " + request.target());
        var command = mapper.toCommand(request);
        commandUseCase.create(command);


        return ResponseEntity.ok().body(ApiResponseType.CREATION_SUCCESS.withParams("Notification"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        int rowsDeleted = commandUseCase.delete(id);
        return ResponseEntity.ok().body(ApiResponseType.NOTIFICATIONS_DELETE_SUCCESS_ADMIN.withParams(id, rowsDeleted));
    }

    @GetMapping
    public PageResult<NotificationReadModel> all(
            Pageable pageable,
            @ModelAttribute AdminNotificationQueryRequest request
    ) {
        System.out.println(pageable);
        PageQuery pageQuery = PageQueryAssembler.fromPageable(pageable, AdminNotificationPagingPolicy.INSTANCE);
        AdminNotificationQuery query = mapper.toQuery(request);
        return queryUseCase.getAll(pageQuery, query);
    }
}
