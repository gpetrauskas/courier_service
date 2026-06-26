import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaymentInfo } from "../models/payment/payment-info.model";
import { PaymentRequest } from "../models/payment/payment-request.model";
import {PaymentSectionRequest} from "../models/payment/payment-section-request.dto";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private baseURL = `${environment.apiUrl}/api/payment`;

  constructor(private http: HttpClient) { }

  getPaymentInfoByOrderId(orderId: number): Observable<PaymentInfo> {
    return this.http.get<PaymentInfo>(`${this.baseURL}/${orderId}`, { withCredentials: true });
  }

  processPayment(orderId: number, paymentRequestDTO: PaymentRequest): Observable<string> {
    return this.http.post<string>(`${this.baseURL}/${orderId}/pay`, paymentRequestDTO, {
      withCredentials: true
    });
  }

  updatePaymentSection(orderId: number, request: PaymentSectionRequest): Observable<void> {
    return this.http.patch<void>(`${this.baseURL}/${orderId}/updateSection`, request, { withCredentials: true });
  }

}
