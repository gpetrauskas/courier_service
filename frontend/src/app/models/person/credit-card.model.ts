import { PaymentMethod } from "./payment-method.model";

export interface CreditCard extends PaymentMethod {
  last4: string;
}
