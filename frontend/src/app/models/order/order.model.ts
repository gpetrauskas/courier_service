import { Address } from '../address/address.model';
import { PackageDetails } from './package-details.model';

export interface OrderDTO {
  senderAddress: Address;
  recipientAddress: Address;
  preferenceId: number | null;
  parcelDetails: PackageDetails;
}
