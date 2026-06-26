import {MyInfo} from "./my-info.model";

export interface MyInfoAdmin extends MyInfo {
  createdTasks: number;
}
