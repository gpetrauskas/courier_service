package gytis.courier.application.port.in.task;

import gytis.courier.application.command.CreateTaskCommand;

import java.util.List;

public interface AdminTaskCommandUseCase {
    void createTask(CreateTaskCommand command);
    void addItems(Long taskId, List<Long> parcelIds);
    void cancel(Long taskId, Long adminId);
    void complete(Long taskId);
    void removeItem(Long taskId, Long itemId, Long adminId);
    void changeCourier(Long taskId, Long courierId);
}
