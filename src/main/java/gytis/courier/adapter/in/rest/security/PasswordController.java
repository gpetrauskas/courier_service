package gytis.courier.adapter.in.rest.security;

import gytis.courier.adapter.in.rest.security.dto.PasswordChangeRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.security.ChangePasswordUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@PreAuthorize("isAuthenticated()")
public class PasswordController {
    private final ChangePasswordUseCase changePassword;
    private final SecurityRequestMapper requestMapper;

    public PasswordController(ChangePasswordUseCase changePassword, SecurityRequestMapper requestMapper) {
        this.changePassword = changePassword;
        this.requestMapper = requestMapper;
    }

    @PatchMapping
    public ResponseEntity<ApiResponse> changePassword(@RequestBody PasswordChangeRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        changePassword.changePassword(requestMapper.toChangePasswordCommand(request, person.id()));
        return ResponseEntity.ok(new ApiResponse("success", "Password changed successfully"));
    }
}
