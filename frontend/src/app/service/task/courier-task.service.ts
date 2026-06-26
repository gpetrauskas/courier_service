import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { PaginatedResponse } from "../../models/paginated-response.model";
import { CourierTask } from "../../models/task/courier-task.model";
import { ApiResponse } from "../../models/api-response.dto";
import { DeliveryTaskItemStatus } from "../../models/delivery-task-item-status.model";
import { Params } from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class CourierTaskService {
  private taskUrl = `${environment.apiUrl}/api/courier/tasks`;

  constructor(private http: HttpClient) { }

  assigned():Observable<PaginatedResponse<CourierTask>> {
    return this.http.get<PaginatedResponse<CourierTask>>(`${this.taskUrl}/assigned`, { withCredentials: true });
  }

  items(taskId: number): Observable<CourierTask> {
    return this.http.get<CourierTask>(`${this.taskUrl}/${taskId}/current`, { withCredentials: true });
  }

  addNote(taskId: number, itemId: number, note: string) {
    const request = { note };
    console.log("note: " + note);
    return this.http.post<ApiResponse>(`${this.taskUrl}/${taskId}/items/${itemId}/notes`, request, { withCredentials: true });
  }

  updateItemStatus(taskId: number, itemId: number, status: DeliveryTaskItemStatus) {
    const request = { status };
    return this.http.patch<ApiResponse>(`${this.taskUrl}/${taskId}/items/${itemId}/status`, request, { withCredentials: true });
  }

  history(params: Params) {
    return this.http.get<PaginatedResponse<CourierTask>>(`${this.taskUrl}/history`, { params: params, withCredentials: true });
  }

  checkIn(taskId: number) {
    return this.http.patch<ApiResponse>(`${this.taskUrl}/${taskId}/check-in`, { withCredentials: true });
  }
}
