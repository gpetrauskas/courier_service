package gytis.courier.adapter.in.rest.task;

import gytis.courier.adapter.in.rest.task.dto.ChangeCourierRequest;
import gytis.courier.adapter.in.rest.task.dto.CreateTaskRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.task.AdminTaskCommandUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskCommandController {
    private final AdminTaskCommandUseCase useCase;
    private final TaskRequestMapper requestMapper;

    public AdminTaskCommandController(AdminTaskCommandUseCase useCase, TaskRequestMapper requestMapper) {
        this.useCase = useCase;
        this.requestMapper = requestMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createTask(@RequestBody CreateTaskRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        useCase.createTask(requestMapper.toCreateCommand(request, person.id()));
        System.out.println("cia " + request.type());
        return ResponseEntity.ok(new ApiResponse("success", "New task created successfully"));
    }

    @PostMapping("/{taskId}/items")
    public ResponseEntity<ApiResponse> addItem(@PathVariable Long taskId, @RequestBody List<Long> parcelIds) {
        useCase.addItems(taskId, parcelIds);
        return ResponseEntity.ok(new ApiResponse("success", parcelIds.size() + " item(s) added successfully"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancel(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        useCase.cancel(id, person.id());
        return ResponseEntity.ok(new ApiResponse("success", "Task " + id + " was successfully canceled"));
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse> complete(@PathVariable Long taskId) {
        System.out.println("alio");
        useCase.complete(taskId);
        return ResponseEntity.ok(new ApiResponse("success", "Task " + taskId + " was market as completed"));
    }

    @DeleteMapping("/{taskId}/items/{itemId}")
    public ResponseEntity<ApiResponse> removeItem(@PathVariable Long taskId, @PathVariable Long itemId, @AuthenticationPrincipal AuthenticatedPerson person) {
        useCase.removeItem(taskId, itemId, person.id());
        return ResponseEntity.ok(new ApiResponse("success", String.format("Item %d was removed successfully", itemId)));
    }

    @PatchMapping("/{taskId}/courier")
    public ResponseEntity<ApiResponse> changeCourier(@PathVariable Long taskId, @RequestBody ChangeCourierRequest request) {
        System.out.println(taskId + " " + request.courierId());

        useCase.changeCourier(taskId, request.courierId());
        return ResponseEntity.ok(new ApiResponse("success", "Courier was changed successfully"));
    }
}
