import {TaskType} from "../../enums/task-type.enum";

export interface CreateTask {
  courierId: number,
  parcelIds: number[],
  type: TaskType
}
