import {ParcelStatus} from "../../enums/parcel-status.enum";

export interface AdminOrderTask {
  orderId: number;
  parcelId: number;
  parcelStatus: ParcelStatus;
  contents: string;
  failuresCount: number;
  deliveryMethodName: string;
  weight: string;
  dimensions: string;
  fullAddress: string;
  customerContacts: string;
  createDate: string;
}
