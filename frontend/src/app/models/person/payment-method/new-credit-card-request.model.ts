import {PaymentMethodRequest} from "../payment-method-request.model";

export interface NewCreditCardRequest extends PaymentMethodRequest {
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
  saveCard: boolean;
}
