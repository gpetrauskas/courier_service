package gytis.courier.application.service.security;

import gytis.courier.application.port.in.security.ChangePasswordUseCase;
import gytis.courier.application.command.PasswordChangeCommand;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.domain.person.Person;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordChangeService implements ChangePasswordUseCase {
    private final PasswordEncoder passwordEncoder;
    private final PersonCommandPort managementPort;

    public PasswordChangeService(PasswordEncoder passwordEncoder, PersonCommandPort managementPort) {
        this.passwordEncoder = passwordEncoder;
        this.managementPort = managementPort;
    }

    @Override
    public void changePassword(PasswordChangeCommand command) {
        Person person = managementPort.findById(command.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        if (!passwordEncoder.matches(command.currentPassword(), person.getPassword())) {
            throw new IllegalStateException("Current password is incorrect");
        }

        person.updatePasswordHash(passwordEncoder.encode(command.newPassword()));
        managementPort.save(person);
    }
}
