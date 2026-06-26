package gytis.courier.application.port.in.security;

import gytis.courier.application.command.PasswordChangeCommand;

public interface ChangePasswordUseCase {
    void changePassword(PasswordChangeCommand command);
}
