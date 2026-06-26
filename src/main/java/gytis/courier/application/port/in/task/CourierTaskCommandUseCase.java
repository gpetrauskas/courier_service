package gytis.courier.application.port.in.task;

import gytis.courier.application.command.AddItemNoteCommand;
import gytis.courier.application.service.task.UpdateItemStatusCommand;

public interface CourierTaskCommandUseCase {
    void updateItemStatus(UpdateItemStatusCommand command);
    void addItemNote(AddItemNoteCommand command);
    void checkIn(Long taskId, Long myId);
}
