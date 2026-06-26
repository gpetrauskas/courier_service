package gytis.courier.domain.delivery;

import gytis.courier.application.service.delivery.CreateDeliveryOptionCommand;
import gytis.courier.application.service.delivery.UpdateDeliveryOptionCommand;
import gytis.courier.domain.person.Role;

import java.math.BigDecimal;
import java.util.Objects;

public record DeliveryOption(Long id, String name, String description, BigDecimal price, boolean disabled) {
    public DeliveryOption {
        validateName(name);
        validateDescription(description);
        validatePrice(price);
    }

    public static DeliveryOption create(CreateDeliveryOptionCommand command) {
        return new DeliveryOption(null, command.name(), command.description(), command.price(), command.disabled());
    }

    public DeliveryOption disable() {
        return (this.disabled)
                ? this
                : new DeliveryOption(this.id, this.name, this.description, this.price, true);
    }

    public DeliveryOption update(UpdateDeliveryOptionCommand command) {
        String newName = command.name() != null ? command.name() : this.name;
        String newDescription = command.description() != null ? command.description() : this.description;
        BigDecimal newPrice = command.price() != null ? command.price() : this.price;

        if (Objects.equals(newName, this.name)
                && Objects.equals(newDescription, this.description)
                && Objects.equals(newPrice, this.price)) {

            return this;
        }

        return new DeliveryOption(this.id, newName, newDescription, newPrice, this.disabled);
    }

    public boolean isAvailableFor(Role role) {
        return Role.ADMIN.equals(role) || !this.disabled;
    }

    private void validateName(String name) {
        Objects.requireNonNull(name);
        if (trim(name).isEmpty() || name.length() > 20) {
            throw new IllegalArgumentException("Method name cannot be empty or contain more than 20 characters");
        }
    }

    private void validateDescription(String description) {
        Objects.requireNonNull(description);
        if (trim(description).isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Method description cannot be empty or contain more than 50 characters");
        }
    }

    private void validatePrice(BigDecimal price) {
        Objects.requireNonNull(price);
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be lower than 0");
        }
    }

    private String trim(String s) {
        return s.trim();
    }
}
