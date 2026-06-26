import { Address } from '../address/address.model';

export interface PersonDetailsDTO {
  id: number;
  name: string;
  email: string;
  defaultAddress?: Address;
  role?: string;
  phoneNumber?: string;
  blocked?: boolean;
  deleted?: boolean;
  createdTasks?: number;
  completedDeliveries?: number;
  orderCount?: number;
  subscribed?: boolean;
  hasActiveTask?: boolean;
}
