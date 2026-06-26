package gytis.courier.domain.person;

import gytis.courier.application.command.UserSelfEditCommand;
import gytis.courier.domain.payment.ProviderType;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Person {
    private Long defaultAddressId;
    private List<PaymentMethod> paymentMethods;
    private boolean subscribed;

    protected User() {}

    public User(Long id, String name, Email email, String password) {
        super(id, name, email, password);
        this.paymentMethods = new ArrayList<>();
    }

    @Override
    public String getRole() { return "USER"; }

    @Override
    public void delete(DeletionPolicy policy) {
        if (!policy.canDeleteUser(this.getId())) {
            throw new IllegalArgumentException("User cannot be deleted with active orders");
        }

        markDeleted();
    }

    public Long getDefaultAddressId() { return defaultAddressId; }

    public boolean isSubscribed() { return subscribed; }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void removePaymentMethod(Long methodId) {
        Objects.requireNonNull(methodId);
        boolean removed = paymentMethods.removeIf(m -> m.getId().equals(methodId));
        if (!removed) throw new ResourceNotFoundException("Payment method not found");
    }

    public List<PaymentMethod> getPaymentMethodsByType(ProviderType type) {
        return paymentMethods.stream()
                .filter(pm -> pm.providerType().equals(type))
                .toList();
    }

    public void selfUpdate(UserSelfEditCommand command) {
        Objects.requireNonNull(command);
        System.out.println("cia addressid: " + command.defaultAddressId());

        if (command.phoneNumber() != null) updatePhoneNumber(command.phoneNumber());
        if (command.defaultAddressId() != null) updateDefaultAddress(command.defaultAddressId());
        if (command.subscribed() != null) this.subscribed = command.subscribed();

    }

    public void updateDefaultAddress(Long id) {
        Objects.requireNonNull(id);
        System.out.println("cia id addr " + id);
        this.defaultAddressId = id;
    }

    public PaymentMethod getMethodById(Long methodId) {
        return this.paymentMethods.stream()
                .filter(pm -> pm.getId().equals(methodId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found in users list by " + methodId + " id"));
    }

    public boolean maybeSaveMethod(PaymentMethod method, String token) {
        if (token == null || token.isBlank()) return false;

        boolean exists = paymentMethods.stream()
                        .anyMatch(existing ->
                                existing.providerType() == method.providerType()
                                        && Objects.equals(existing.getToken(), token)
                        );

            if (exists) return false;

            method.setToken(token);
            this.paymentMethods.add(method);
            return true;
    }
}
