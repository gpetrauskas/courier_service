import { Address } from '../address/address.model';

export interface PersonResponseDTO {
  id: number;
  name: string;
  email: string;
  isBlocked: boolean;
  isDeleted: boolean;
  phoneNumber: string | null;
  role: string;
  defaultAddress: Address | null;
  addresses: Address[];
  orderCount: number;
  subscribed: boolean;
}
