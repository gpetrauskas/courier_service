package gytis.courier.adapter.in.rest.delivery;

import gytis.courier.adapter.in.rest.delivery.dto.CreateDeliveryOptionRequest;
import gytis.courier.adapter.in.rest.delivery.dto.UpdateDeliveryMethodRequest;
import gytis.courier.application.port.in.delivery.AddDeliveryOptionUseCase;
import gytis.courier.application.port.in.delivery.DeleteDeliveryOptionUseCase;
import gytis.courier.application.port.in.delivery.DeliveryOptionQueryUseCase;
import gytis.courier.application.port.in.delivery.UpdateDeliveryOptionUseCase;
import gytis.courier.application.readmodel.deliveryoption.DeliveryOptionReadModel;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.adapter.in.rest.common.ApiResponseType;
import gytis.courier.domain.delivery.DeliveryGroup;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery-options")
public class DeliveryOptionController {
    private final DeliveryOptionQueryUseCase queryUseCase;
    private final AddDeliveryOptionUseCase addUseCase;
    private final DeleteDeliveryOptionUseCase deleteUseCase;
    private final UpdateDeliveryOptionUseCase updateUseCase;
    private final DeliveryOptionMapper mapper;

    public DeliveryOptionController(DeliveryOptionQueryUseCase queryUseCase, AddDeliveryOptionUseCase addUseCase, DeleteDeliveryOptionUseCase deleteUseCase, UpdateDeliveryOptionUseCase updateUseCase, DeliveryOptionMapper mapper) {
        this.queryUseCase = queryUseCase;
        this.addUseCase = addUseCase;
        this.deleteUseCase = deleteUseCase;
        this.updateUseCase = updateUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<DeliveryGroup, List<DeliveryOptionReadModel>>> getUserView() {
        return ResponseEntity.ok(queryUseCase.getAllCategorized());
    }

    @GetMapping("/notCategorized")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryOptionReadModel> getAllNotCategorized() {
        return queryUseCase.getAllNotCategorized();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateDeliveryMethodRequest request) {
        var command = mapper.toUpdateCommand(id, request);
        updateUseCase.updateDeliveryMethod(command);
        return ResponseEntity.ok(ApiResponseType.DELIVERY_OPTION_UPDATE_SUCCESS.withParams(id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateDeliveryOptionRequest request) {
        addUseCase.add(mapper.toCreateCommand(request));
        return ResponseEntity.ok(ApiResponseType.CREATION_SUCCESS.withParams("Delivery option"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        deleteUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseType.DELETION_SUCCESS.withParams("Delivery option", id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DeliveryOptionReadModel get(@PathVariable Long id) {
        return queryUseCase.getById(id);
    }
}
