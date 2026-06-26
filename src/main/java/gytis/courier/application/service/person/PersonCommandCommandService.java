package gytis.courier.application.service.person;

import gytis.courier.application.command.UpdatePersonCommand;
import gytis.courier.application.port.in.person.PersonCommandUseCase;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.domain.person.DeletionPolicy;
import gytis.courier.domain.person.Person;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonCommandCommandService implements PersonCommandUseCase {
    private final PersonCommandPort managementPort;
    private final DeletionPolicy deletionPolicy;

    public PersonCommandCommandService(PersonCommandPort managementPort, DeletionPolicy deletionPolicy) {
        this.managementPort = managementPort;
        this.deletionPolicy = deletionPolicy;
    }

    @Override
    public void updatePersonDetails(UpdatePersonCommand command, Long id) {
        Person person = managementPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No person found"));

        System.out.println(person.getId() + " "+ person.getRole() + person.isAdmin() +
               person.getEmail());

        person.update(command);

        managementPort.save(person);
    }

    @Override
    public void deletePerson(Long id) {
        Person person = managementPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        person.delete(deletionPolicy);

        managementPort.save(person);
    }
}
