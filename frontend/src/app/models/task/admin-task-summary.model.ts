import { TaskType } from '../../enums/task-type.enum';
import { TaskStatus } from '../../enums/task-status.enum';

export interface AdminTaskSummary {
  id: number;
  courierId: number;
  taskType: TaskType;
  deliveryStatus: TaskStatus;
  createdAt: Date;
  completedAt: Date;
}
