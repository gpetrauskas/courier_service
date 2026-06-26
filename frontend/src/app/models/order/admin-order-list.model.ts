import { OrderStatus } from "../../enums/order-status.enum";

export interface AdminOrderList {
  id: number;
  userId: number;
  deliveryMethodName: string;
  status: OrderStatus;
  createDate: string;
}
