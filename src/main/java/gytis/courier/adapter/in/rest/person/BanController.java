package gytis.courier.adapter.in.rest.person;

import gytis.courier.adapter.in.rest.person.dto.BanActionRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.person.BanPersonUseCase;
import gytis.courier.application.readmodel.person.BanHistoryReadModel;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban")
@PreAuthorize("hasRole('ADMIN')")
public class BanController {
    private final BanPersonUseCase banPerson;

    public BanController(BanPersonUseCase banPerson) {
        this.banPerson = banPerson;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> banUnban(
            @PathVariable Long id,
            @RequestBody(required = false) BanActionRequest request,
            @AuthenticationPrincipal AuthenticatedPerson person) {
        String reason = (request != null) ? request.reason() : "No reason provided";
        return ResponseEntity.ok(new ApiResponse("success", banPerson.banUnban(id, reason, person.email())));
    }

    @GetMapping("/{id}/history")
    public List<BanHistoryReadModel> history(@PathVariable Long id) {
        return banPerson.getBanHistory(id);
    }
}
