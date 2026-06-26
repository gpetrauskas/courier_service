import { Address } from '../address/address.model';
import { PersonDetailsDTO } from '../person/person-details.model';
import { OrderStatus } from "../../enums/order-status.enum";
import { AdminPaymentInfo } from "../payment/admin-payment-info.model";
import { AdminParcelDetail } from "./admin-parcel-detail.model";

export interface AdminOrder {
  id: number;
  deliveryMethodName: string;
  status: OrderStatus;
  createDate: string;
  payment: AdminPaymentInfo;
  user: PersonDetailsDTO;
  sender: Address;
  recipient: Address;
  parcel: AdminParcelDetail;
}
