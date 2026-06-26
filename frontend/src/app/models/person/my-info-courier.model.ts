import {MyInfo} from "./my-info.model";

export interface MyInfoCourier extends MyInfo {
  activeTask: boolean;
}
