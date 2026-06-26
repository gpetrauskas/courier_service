package gytis.courier.application.command;

public record PasswordChangeCommand(
        Long personId,
        String newPassword,
        String currentPassword
) {
}
