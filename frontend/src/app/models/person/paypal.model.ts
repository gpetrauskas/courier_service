import { PaymentMethod } from "./payment-method.model";

export interface Paypal extends PaymentMethod {
  ppEmail: string;
}
