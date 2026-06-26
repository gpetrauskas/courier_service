import { OrderStatus } from "../../enums/order-status.enum";
import { ParcelStatus } from "../../enums/parcel-status.enum";

export interface UserOrderDetails {
  id: number;
  deliveryMethodName: string;
  status: OrderStatus;
  createDate: string;
  senderAddress: string;
  recipientAddress: string;
  contents: string;
  weightName: string;
  dimensionsName: string;
  trackingNumber: string;
  parcelStatus: ParcelStatus;
}
