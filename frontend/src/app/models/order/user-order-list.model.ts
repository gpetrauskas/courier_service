import { OrderStatus } from "../../enums/order-status.enum";

export interface UserOrderList {
  id: number;
  deliveryMethodName: string;
  status: OrderStatus;
  createDate: string;
}
