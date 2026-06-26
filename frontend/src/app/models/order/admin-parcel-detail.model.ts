import { ParcelStatus } from "../../enums/parcel-status.enum";

export interface AdminParcelDetail {
  id: number;
  contents: string;
  weight: string;
  dimensions: string;
  trackingNumber: string;
  status: ParcelStatus;
}
