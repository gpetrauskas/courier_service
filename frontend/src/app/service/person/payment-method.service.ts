import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {PaymentMethod} from "../../models/person/payment-method.model";

@Injectable({
  providedIn: 'root'
})
export class PaymentMethodService {
  private baseURL = `${environment.apiUrl}/api/me`;
  constructor(private http: HttpClient) { }

  fetchUserSavedPaymentMethods(): Observable<PaymentMethod[]> {
    return this.http.get<PaymentMethod[]>(`${this.baseURL}/payment-methods`, { withCredentials: true });
  }

  deletePaymentMethod(paymentMethodId: number): Observable<{ [key: string]: string }> {
    return this.http.delete<{ [key: string]: string }>(`${this.baseURL}/payment-methods/${paymentMethodId}`,
     { withCredentials: true });
  }
}
