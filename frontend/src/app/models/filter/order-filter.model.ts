import { OrderStatus } from "../../enums/order-status.enum";

export interface OrderFilterModel {
  kind: 'order';
  userId: number | null;
  orderStatus: OrderStatus;
}
