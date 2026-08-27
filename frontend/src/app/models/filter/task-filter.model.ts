import { TaskType } from "../../enums/task-type.enum";
import { DeliveryStatus } from "../../enums/delivery-status.enum";

export interface TaskFilterModel {
  kind: 'task';
  taskId: number | null;
  courierId: number | null;
  taskType: TaskType;
  deliveryStatus: DeliveryStatus;
}
