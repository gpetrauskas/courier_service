import { DeliveryTaskItem } from './delivery-task-item.model';
import { TaskStatus } from '../../enums/task-status.enum';
import { TaskType } from '../../enums/task-type.enum';

export interface AdminTaskDetailed {
    id: number;
    taskType: TaskType;
    deliveryStatus: TaskStatus;
    createdAt: Date;
    completedAt: Date;
    courierId: number;
    courierName: string;
    courierPhoneNumber: string;
    items: DeliveryTaskItem[];
}
