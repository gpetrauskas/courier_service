package gytis.courier.application.port.in.person;

import gytis.courier.application.command.UpdatePersonCommand;

public interface PersonCommandUseCase {
    void updatePersonDetails(UpdatePersonCommand command, Long id);
    void deletePerson(Long id);
}
