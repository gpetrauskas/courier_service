package gytis.courier.adapter.in.rest.payment;

import gytis.courier.adapter.in.rest.payment.dto.PaymentRequest;
import gytis.courier.adapter.in.rest.payment.dto.PaymentSectionUpdateRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.command.PaymentCommand;
import gytis.courier.application.port.in.payment.PayUseCase;
import gytis.courier.application.port.in.payment.PaymentQueryUseCase;
import gytis.courier.application.port.in.payment.PaymentUpdateUseCase;
import gytis.courier.application.readmodel.payment.PayReadModel;
import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PayUseCase payUseCase;
    private final PaymentRequestMapper requestMapper;
    private final PaymentUpdateUseCase updateUseCase;
    private final PaymentQueryUseCase queryUseCase;

    public PaymentController(PayUseCase payUseCase, PaymentRequestMapper requestMapper, PaymentUpdateUseCase updateUseCase, PaymentQueryUseCase queryUseCase) {
        this.payUseCase = payUseCase;
        this.requestMapper = requestMapper;
        this.updateUseCase = updateUseCase;
        this.queryUseCase = queryUseCase;
    }

    @PostMapping("/{orderId}/pay")
    @PreAuthorize("hasRole('USER')")
    public PayReadModel pay(@PathVariable Long orderId, @RequestBody PaymentRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        PaymentCommand command = requestMapper.toCommand(person.id(), orderId, request);
        System.out.println("im here ?");
        return payUseCase.pay(command);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public UserPaymentSummaryReadModel get(@PathVariable Long orderId) {
        return queryUseCase.get(orderId);
    }

    @PatchMapping("{orderId}/updateSection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long orderId, @RequestBody @Valid PaymentSectionUpdateRequest request) {
        updateUseCase.update(orderId, requestMapper.toUpdateCommand(request));
        return ResponseEntity.ok().build();
    }
}
