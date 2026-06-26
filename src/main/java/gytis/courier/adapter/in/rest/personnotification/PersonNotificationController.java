package gytis.courier.adapter.in.rest.personnotification;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PersonNotificationPageResult;
import gytis.courier.application.port.in.personnotification.PersonNotificationCommandUseCase;
import gytis.courier.application.port.in.personnotification.PersonNotificationQueryUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/me/notifications")
public class PersonNotificationController {
    private final PersonNotificationCommandUseCase commandUseCase;
    private final PersonNotificationQueryUseCase queryUseCase;

    public PersonNotificationController(PersonNotificationCommandUseCase commandUseCase, PersonNotificationQueryUseCase queryUseCase) {
        this.commandUseCase = commandUseCase;
        this.queryUseCase = queryUseCase;
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        commandUseCase.markAsRead(id, person.id());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal AuthenticatedPerson person) {
        commandUseCase.markAllAsRead(person.id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {

        throw new RuntimeException("test error");
        /*        commandUseCase.delete(id, person.id());
        return ResponseEntity.noContent().build();*/
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal AuthenticatedPerson person) {
        commandUseCase.deleteAll(person.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PersonNotificationPageResult get(Pageable pageable, @AuthenticationPrincipal AuthenticatedPerson person) {
        PageQuery pageQuery = PageQueryAssembler.fromPageable(pageable, PersonNotificationPagingPolicy.INSTANCE);
        return queryUseCase.getAll(pageQuery, person.id());
    }

    @GetMapping("/indexed/{nId}")
    public PersonNotificationPageResult getPageContainingNotification(
            @PathVariable Long nId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(40) int pageSize,
            @AuthenticationPrincipal AuthenticatedPerson person) {
        return queryUseCase.getPageContainingNotification(nId, person.id(), pageSize);
    }
}
