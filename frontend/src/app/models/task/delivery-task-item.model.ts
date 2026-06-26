import { ParcelStatus } from '../../enums/parcel-status.enum';

export interface DeliveryTaskItem {
  id: number;
  parcelId: number;
  parcelStatus: ParcelStatus;
  deliveryMethodName: string;
  contents: string;
  failuresCount: number;
  weight: string;
  dimensions: string;
  address: string;
}
