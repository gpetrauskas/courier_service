import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { DeliveryOption } from '../models/delivery-option/delivery-option.model';
import { DeliveryGroup } from '../enums/delivery-group.enum';
import { CreateDeliveryOption } from '../models/delivery-option/create-delivery-option.dto';
import { ApiResponse } from '../models/api-response.dto';

@Injectable({
  providedIn: 'root'
})
export class DeliveryOptionsService {
  private apiUrl = `${environment.apiUrl}/api/delivery-options`;

  constructor(private http: HttpClient) { }

  getDeliveryOptions(): Observable<Record<DeliveryGroup, DeliveryOption[]>> {
    return this.http.get<Record<DeliveryGroup, DeliveryOption[]>>(`${this.apiUrl}`, { withCredentials: true });
  }

  getAllNotCategorized(): Observable<DeliveryOption[]> {
    return this.http.get<DeliveryOption[]>(`${this.apiUrl}/notCategorized`, { withCredentials: true });
  }

  update(id: number, updatedDeliveryOption: Partial<DeliveryOption>): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(`${this.apiUrl}/${id}`, updatedDeliveryOption, {
      withCredentials: true
    });
  }

  add(newDeliveryOption: CreateDeliveryOption): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}`, newDeliveryOption, { withCredentials: true });
  }

  delete(id: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

}
