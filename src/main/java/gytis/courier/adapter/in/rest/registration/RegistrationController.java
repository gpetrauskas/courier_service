package gytis.courier.adapter.in.rest.registration;

import gytis.courier.adapter.in.rest.registration.dto.RegistrationRequest;
import gytis.courier.application.port.in.registration.RegisterUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {
    private final RegisterUseCase useCase;
    private final RegistrationMapper mapper;

    public RegistrationController(RegisterUseCase useCase, RegistrationMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegistrationRequest request) {
        useCase.register(mapper.toCommand(request));
        return ResponseEntity.ok(new ApiResponse("success", "Registration was successful"));
    }
}
