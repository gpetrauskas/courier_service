import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ApiResponse } from "../../models/api-response.dto";
import { CreateTask } from "../../models/task/create-task.model";
import { PaginatedResponse } from "../../models/paginated-response.model";
import { AdminTaskSummary } from "../../models/task/admin-task-summary.model";
import { AdminTaskDetailed } from "../../models/task/admin-task-detailed.model";

@Injectable({
  providedIn: 'root'
})
export class AdminTaskService {
  private taskUrl = `${environment.apiUrl}/api/admin/tasks`;

  constructor(private http: HttpClient) { }

  create(task: CreateTask): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.taskUrl}`, task, { withCredentials: true });
  }

  changeCourier(taskId: number, courierId: number): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(`${this.taskUrl}/${taskId}/courier`, { courierId }, { withCredentials: true })
  }

  removeItem(taskId: number, itemId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(
      `${this.taskUrl}/${taskId}/items/${itemId}`, { withCredentials: true }
    );
  }

  getAllDeliveryTaskLists(params: { page: number, size: number, courierId?: number, taskListId?: number,
    taskType?: string, deliveryStatus?: string, sortBy: string, direction: string }): Observable<PaginatedResponse<AdminTaskSummary>> {

    const queryParams = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });

    console.log(queryParams);

    return this.http.get<PaginatedResponse<AdminTaskSummary>>(`${this.taskUrl}?${queryParams.toString()}`, { withCredentials: true });
  }

  getDetailedTask(taskId: number): Observable<AdminTaskDetailed> {
    return this.http.get<AdminTaskDetailed>(`${this.taskUrl}/${taskId}`, { withCredentials: true });
  }

  cancel(taskId: number): Observable<void> {
    return this.http.patch<void>(`${this.taskUrl}/${taskId}/cancel`, { withCredentials: true });
  }

  complete(taskId: number) {
    return this.http.patch<ApiResponse>(`${this.taskUrl}/${taskId}/complete`, { withCredentials: true });
  }

}
