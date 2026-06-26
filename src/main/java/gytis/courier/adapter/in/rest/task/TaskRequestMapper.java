package gytis.courier.adapter.in.rest.task;

import gytis.courier.adapter.in.rest.task.dto.AddItemNoteRequest;
import gytis.courier.adapter.in.rest.task.dto.CreateTaskRequest;
import gytis.courier.adapter.in.rest.task.dto.UpdateItemStatusRequest;
import gytis.courier.application.command.CreateTaskCommand;
import gytis.courier.application.command.AddItemNoteCommand;
import gytis.courier.application.service.task.UpdateItemStatusCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskRequestMapper {
    @Mapping(target = "taskType", source = "request.type")
    CreateTaskCommand toCreateCommand(CreateTaskRequest request, Long adminId);
    @Mapping(target = "note", source = "request.note")
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "myId", source = "myId")
    AddItemNoteCommand toAddItemNoteCommand(Long taskId, Long itemId, AddItemNoteRequest request, Long myId);
    UpdateItemStatusCommand toUpdateItemStatusCommand(Long taskId, Long taskItemId, UpdateItemStatusRequest request, Long myId);
}
