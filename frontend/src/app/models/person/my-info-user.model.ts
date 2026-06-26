import { MyInfo } from "./my-info.model";

export interface MyInfoUser extends MyInfo {
  orderCount: number;
  subscribed: boolean;
  defaultAddress: string;
  phoneNumber: string;
}
