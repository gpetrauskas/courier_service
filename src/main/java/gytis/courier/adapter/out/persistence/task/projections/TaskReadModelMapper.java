package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.application.readmodel.task.CTaskListReadModel;
import gytis.courier.application.readmodel.task.CTaskReadModel;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskItemReadModel;
import gytis.courier.application.readmodel.task.CTaskHistoryReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import gytis.courier.application.readmodel.task.CTaskItemReadModel;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskReadModelMapper {
    //admin
    AdminTaskReadModel toAdminDetailed(AdminTaskHeaderProjection header, List<AdminTaskItemReadModel> items);
    @Mapping(target = "address",expression = "java(resolveTargetAddress(p.getSenderAddress(), p.getRecipientAddress(), p.getStatus()))")
    @Mapping(target = "parcelStatus", source = "status")
    AdminTaskItemReadModel toItemAdmin(AdminTaskItemProjection p);

    default String resolveTargetAddress(String sender, String recipient, ParcelStatus parcelStatus) {
        return parcelStatus == ParcelStatus.PICKING_UP
                ? sender
                : recipient;
    }

    //courier
    CTaskHistoryReadModel toCourierHistory(CourierTaskHistoryProjection projection);
    @Mapping(target = "taskId", source = "id")
    CTaskListReadModel toListReadModel(TaskListProjection projection);
    @Mapping(target = "relevantAddress", expression = "java(resolveTargetAddress(p.getSenderAddress(), p.getRecipientAddress(), taskType))")
    @Mapping(target = "relevantContacts", expression = "java(resolveTargetContacts(p.getSenderContacts(), p.getRecipientContacts(), taskType))")
    @Mapping(target = "status", source = "p.parcelStatus")
    CTaskItemReadModel toCourierItem(CourierTaskItemProjection p, TaskType taskType);
    @Mapping(target = "taskId", source = "header.id")
    CTaskReadModel toCourierCurrentDetailed(CourierTaskHeaderProjection header, List<CTaskItemReadModel> items);

    TaskListReadModel toTaskListReadModel(TaskListProjection projection);


    default String resolveTargetAddress(String sender, String recipient, TaskType taskType) {
        return taskType == TaskType.PICKUP
                ? sender
                : recipient;
    }

    default String resolveTargetContacts(String sender, String recipient, TaskType taskType) {
        return taskType == TaskType.PICKUP
                ? sender
                : recipient;
    }

}
