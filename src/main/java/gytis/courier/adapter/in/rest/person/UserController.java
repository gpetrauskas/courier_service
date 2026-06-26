package gytis.courier.adapter.in.rest.person;

import gytis.courier.adapter.in.rest.address.AddressRequestMapper;
import gytis.courier.adapter.in.rest.address.dto.AddressRequest;
import gytis.courier.adapter.in.rest.person.dto.UserSelfEditRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.command.PartialAddressUpdateCommand;
import gytis.courier.application.port.in.address.AddressQueryUseCase;
import gytis.courier.application.port.in.address.DeleteAddressUseCase;
import gytis.courier.application.port.in.address.UpdateAddressUseCase;
import gytis.courier.application.port.in.paymentmethod.DeletePaymentMethodUseCase;
import gytis.courier.application.port.in.paymentmethod.PaymentMethodQueryUseCase;
import gytis.courier.application.port.in.person.EditMyInfoUseCase;
import gytis.courier.application.port.in.person.PersonQueryUseCase;
import gytis.courier.application.readmodel.address.AddressReadModel;
import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.application.readmodel.person.MyInfoReadModel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class UserController {
    private final PaymentMethodQueryUseCase paymentMethodQueryUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;
    private final AddressQueryUseCase addressQueryUseCase;
    private final DeletePaymentMethodUseCase deletePaymentMethodUseCase;
    private final EditMyInfoUseCase editMyInfoUseCase;
    private final AddressRequestMapper addressRequestMapper;
    private final PersonRequestMapper personRequestMapper;
    private final PersonQueryUseCase personQueryUseCase;

    public UserController(PaymentMethodQueryUseCase paymentMethodQueryUseCase, UpdateAddressUseCase updateAddressUseCase, DeleteAddressUseCase deleteAddressUseCase, AddressQueryUseCase addressQueryUseCase, DeletePaymentMethodUseCase deletePaymentMethodUseCase, EditMyInfoUseCase editMyInfoUseCase, AddressRequestMapper addressRequestMapper, PersonRequestMapper personRequestMapper, PersonQueryUseCase personQueryUseCase) {
        this.paymentMethodQueryUseCase = paymentMethodQueryUseCase;
        this.updateAddressUseCase = updateAddressUseCase;
        this.deleteAddressUseCase = deleteAddressUseCase;
        this.addressQueryUseCase = addressQueryUseCase;
        this.deletePaymentMethodUseCase = deletePaymentMethodUseCase;
        this.editMyInfoUseCase = editMyInfoUseCase;
        this.addressRequestMapper = addressRequestMapper;
        this.personRequestMapper = personRequestMapper;
        this.personQueryUseCase = personQueryUseCase;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'COURIER')")
    public MyInfoReadModel myInfo(@AuthenticationPrincipal AuthenticatedPerson person) {
        return personQueryUseCase.getMyInfo(person.id(), person.role());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressReadModel>> getMyAddressList(@AuthenticationPrincipal AuthenticatedPerson person) {
        System.out.println("Testinu id " + person.id());
        return ResponseEntity.ok(addressQueryUseCase.getAllMyAddresses(person.id()));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable Long id, @RequestBody AddressRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        PartialAddressUpdateCommand command = addressRequestMapper.toUpdateCommand(request);
        updateAddressUseCase.updateAddress(command, id, person.id());
        return ResponseEntity.ok(new ApiResponse("success", "Address updated successfully"));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse> removeAddress(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        deleteAddressUseCase.deleteAddressById(id, person.id());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/payment-methods")
    public List<UserPaymentMethodReadModel> getSaved(@AuthenticationPrincipal AuthenticatedPerson person) {
        System.out.println("cia " + person.id());
        return paymentMethodQueryUseCase.savedMethods(person.id());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/payment-methods/{id}")
    public UserPaymentMethodReadModel get(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        return paymentMethodQueryUseCase.get(id, person.id());
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/payment-methods/{id}")
    public ResponseEntity<ApiResponse> deletePaymentMethod(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        System.out.println("methodo id " + id);
        deletePaymentMethodUseCase.delete(id, person.id());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/my-info")
    public ResponseEntity<ApiResponse> editMyInfo(@RequestBody @Valid UserSelfEditRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        editMyInfoUseCase.editMyInfo(personRequestMapper.toUserSelfUpdateCommand(request, person.id()));
        return ResponseEntity.ok(new ApiResponse("success", "Info was updated successfully"));
    }
}
