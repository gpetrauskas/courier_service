import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { OrderDTO } from '../../models/order/order.model';
import { OrderSectionRequest } from '../../models/order/order-section-request.dto';
import { ParcelSectionRequest } from '../../models/order/parcel-section-request.dto';
import { AddressSectionRequest } from '../../models/address/address-section-request.dto';
import { AdminOrder } from '../../models/order/admin-order.model';
import { TaskType } from "../../enums/task-type.enum";
import { UserOrderDetails } from "../../models/order/user-order-details.model";
import { PaginatedResponse } from "../../models/paginated-response.model";
import { UserOrderList } from "../../models/order/user-order-list.model";
import {AdminOrderList} from "../../models/order/admin-order-list.model";
import {AdminOrderTask} from "../../models/order/admin-order-task.model";

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private baseURL = `${environment.apiUrl}/api/orders`;

  constructor(private http: HttpClient) { }

  getAdminOrderById(id: number): Observable<AdminOrder> {
    return this.http.get<AdminOrder>(`${this.baseURL}/${id}`, { withCredentials: true });
  }

  getMyOrders(page: number, size: number): Observable<PaginatedResponse<UserOrderList>> {
    return this.http.get<PaginatedResponse<UserOrderList>>(
      `${this.baseURL}/user?page=${page}&size=${size}`,
       { withCredentials: true }
    );
  }

  submitOrder(orderData: OrderDTO): Observable<number> {
    return this.http.post<number>(`${this.baseURL}`, orderData, { withCredentials: true });
  }

  updateOrderSection(id: number, updatedData: OrderSectionRequest): Observable<string> {
    return this.http.patch<string>(`${this.baseURL}/${id}`, updatedData, { responseType: 'text' as 'json', withCredentials: true });
  }

  updateParcelSection(id: number, updatedData: ParcelSectionRequest): Observable<string> {
    return this.http.patch<string>(`${this.baseURL}/${id}/parcelSection`, updatedData, { responseType: 'text' as 'json', withCredentials: true });
  }

  updateAddressSection(id: number, updateData: AddressSectionRequest): Observable<string> {
    return this.http.patch<string>(`${this.baseURL}/${id}/addressSection`, updateData, { withCredentials: true });
  }

  getUserOrderById(orderId: number): Observable<UserOrderDetails> {
    return this.http.get<UserOrderDetails>(`${this.baseURL}/user/${orderId}`, { withCredentials: true });
  }


  getAllOrdersForAdmin(page: number, size: number, status?: string, userId?: number): Observable<PaginatedResponse<AdminOrderList>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) {
      params = params.set('status', status);
    }

    if (userId) {
      params = params.set('userId', userId.toString());
    }

    return this.http.get<PaginatedResponse<AdminOrderList>>(`${this.baseURL}/admin`, { params, withCredentials: true });
  }

  allTaskOrdersByTaskType(page: number, size: number, taskType: TaskType): Observable<PaginatedResponse<AdminOrderTask>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('taskType', taskType);

    return this.http.get<PaginatedResponse<AdminOrderTask>>(`${this.baseURL}/getOrdersForTaskAssignment`, { params, withCredentials: true });
  }
}
