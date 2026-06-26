package gytis.courier.domain.person;

import gytis.courier.application.command.UpdatePersonCommand;
import gytis.courier.domain.personnotification.PersonNotification;

import java.time.LocalDateTime;
import java.util.*;

public abstract class Person {
    private Long id;
    private String name;
    private Email email;
    private PhoneNumber phoneNumber;
    private String password;
    private boolean isBlocked;
    private boolean isDeleted;
    private LocalDateTime deletedDate;

    protected Person() {}

    public Person(Long id, String name, Email email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    protected void restoreFields(Long id, String name, Email email, String password, PhoneNumber phoneNumber,
                                 boolean isBlocked, boolean isDeleted, LocalDateTime deletedDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.deletedDate = deletedDate;
    }

    public abstract String getRole();
    public abstract void delete(DeletionPolicy policy);

    public Long getId() { return id; }
    public String getName() { return name; }
    public Email getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isBlocked() { return isBlocked; }
    public boolean isDeleted() { return isDeleted; }
    public PhoneNumber getPhoneNumber() { return phoneNumber; }
    public LocalDateTime getDeletedDate() { return deletedDate; }
    public boolean isAdmin() { return "ADMIN".equals(getRole()); }


    public boolean hasUnreadNotifications(Collection<PersonNotification> notifications) {
        return notifications.stream()
                .anyMatch(n -> n.getId().personId().equals(this.id) && !n.isRead());
    }

    public void setBlocked(boolean blocked) {
        if (this.isBlocked != blocked) {
            this.isBlocked = blocked;
        }
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    public void update(UpdatePersonCommand command) {
        Objects.requireNonNull(command);
        if (command.name() != null && !command.name().isBlank()) updateName(command.name());
        if (command.email() != null) updateEmail((command.email()));
        if (command.phoneNumber() != null) updatePhoneNumber(command.phoneNumber());
    }

    public void updateName(String name) {
        if (!Objects.equals(this.name, name) && name.length() < 20) {
            this.name = name;
        }
    }

    public void updateEmail(Email email) {
        this.email = email;
    }

    public void updatePhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void banUnban() {
        if (this.isDeleted) {
            throw new IllegalArgumentException("User do not exists");
        }

        this.isBlocked = !this.isBlocked;
    }

    public void updatePasswordHash(String hash) {
        Objects.requireNonNull(hash);
        this.password = hash;
    }

    protected void markDeleted() {
        this.isDeleted = true;
        this.deletedDate = LocalDateTime.now();
    }
}
