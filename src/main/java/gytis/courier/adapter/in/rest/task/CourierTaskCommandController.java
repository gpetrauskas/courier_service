package gytis.courier.adapter.in.rest.task;

import gytis.courier.adapter.in.rest.task.dto.AddItemNoteRequest;
import gytis.courier.adapter.in.rest.task.dto.UpdateItemStatusRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.task.CourierTaskCommandUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courier/tasks")
@PreAuthorize("hasRole('COURIER')")
public class CourierTaskCommandController {
    private final TaskRequestMapper requestMapper;
    private final CourierTaskCommandUseCase useCase;

    public CourierTaskCommandController(TaskRequestMapper requestMapper, CourierTaskCommandUseCase useCase) {
        this.requestMapper = requestMapper;
        this.useCase = useCase;
    }

    @PatchMapping("/{taskId}/items/{itemId}/status")
    public ResponseEntity<ApiResponse> updateItemStatus(
            @PathVariable Long taskId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemStatusRequest request,
            @AuthenticationPrincipal AuthenticatedPerson person
    ) {
        useCase.updateItemStatus(requestMapper.toUpdateItemStatusCommand(taskId, itemId, request, person.id()));
        return ResponseEntity.ok(new ApiResponse("success", "Item status successfully updated to: " + request.status()));
    }

    @PostMapping("/{taskId}/items/{itemId}/notes")
    public ResponseEntity<ApiResponse> addItemNote(
            @PathVariable Long taskId,
            @PathVariable Long itemId,
            @RequestBody AddItemNoteRequest request,
            @AuthenticationPrincipal AuthenticatedPerson person
    ) {
        System.out.println("received note: " + request.note() + itemId);
        useCase.addItemNote(requestMapper.toAddItemNoteCommand(taskId, itemId, request, person.id()));
        return ResponseEntity.ok(new ApiResponse("success", "Note was successfully added to the item"));
    }

    @PatchMapping("/{taskId}/check-in")
    public ResponseEntity<ApiResponse> checkIn(
            @PathVariable Long taskId,
            @AuthenticationPrincipal AuthenticatedPerson person
    ) {
        useCase.checkIn(taskId, person.id());
        return ResponseEntity.ok(new ApiResponse("success", "Courier checked in successfully"));
    }
}
