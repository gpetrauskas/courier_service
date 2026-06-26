import { CourierTaskItem } from './courier-task-item.model';

export interface CourierTask {
  courierId: number;
  taskId: number;
  taskType: string;
  deliveryStatus: string;
  createdAt: string;
  completedAt: string;
  items: CourierTaskItem[];
}
