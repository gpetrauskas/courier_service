import { PaymentMethodRequest } from "../person/payment-method-request.model";

export interface PaymentRequest {
  paymentMethodId?: number;
  newPaymentMethod?: PaymentMethodRequest;
  cvc?: string;
}
