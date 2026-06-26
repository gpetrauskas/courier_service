export interface CourierTaskItem {
  id: number;
  weight: string;
  dimensions: string;
  contents: string;
  deliveryMethodName: string;
  status: string;
  tempStatus?: string;
  notes: string[];
  relevantAddress: string;
  relevantContacts: string;
  position?: number;
}
