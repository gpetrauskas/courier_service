package gytis.courier.domain.person;

import jakarta.validation.ValidationException;

import java.time.LocalDateTime;

public class Courier extends Person {
    private boolean hasActiveTask = false;

    public Courier(Long id, String name, Email email, String password) {
        super(id, name, email, password);
    }

    protected Courier() {}

    public static Courier restore(Long id, String name, Email email, String password, PhoneNumber phoneNumber,
                                  boolean isBlocked, boolean isDeleted, LocalDateTime deletedDate, boolean hasActiveTask) {
        Courier courier = new Courier();
        courier.restoreFields(id, name, email, password, phoneNumber, isBlocked, isDeleted, deletedDate);
        courier.hasActiveTask = hasActiveTask;

        return courier;
    }

    @Override
    public String getRole() {
        return "COURIER";
    }

    @Override
    public void delete(DeletionPolicy policy) {
        if (this.hasActiveTask) {
            throw new IllegalArgumentException("Courier cannot be deleted with active tasks");
        }

        markDeleted();
    }

    public boolean getHasActiveTask() { return hasActiveTask; }

    public void activateTask() {
        if (this.hasActiveTask) {
            throw new ValidationException("Courier " + this.getName() + " already has an active task");
        }

        this.hasActiveTask = true;
    }

    public void completeTask() {
        if (!this.hasActiveTask) {
            throw new ValidationException("Courier " + this.getName() + " already without any active task");
        }
        this.hasActiveTask = false;
    }
}